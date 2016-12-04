package com.spbau.bibaev.homework.torrent.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.client.api.StateChangedListener;
import com.spbau.bibaev.homework.torrent.client.download.DownloadManager;
import com.spbau.bibaev.homework.torrent.client.impl.ClientStateImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.client.repl.ReadEvalPrintLoop;
import com.spbau.bibaev.homework.torrent.client.ui.MainWindow;
import com.spbau.bibaev.homework.torrent.common.Details;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientEntryPoint {
  private static final Logger LOG = LogManager.getLogger(ClientEntryPoint.class);

  public static void main(String[] args) {
    final ArgumentParser parser = createParser();
    Namespace parsingResult;
    try {
      parsingResult = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      return;
    }

    final String serverAddress = parsingResult.getString("address");
    final int serverPort = parsingResult.getInt("port");
    final int clientPort = parsingResult.getInt("listen");
    final Path configPath = Paths.get(parsingResult.getString("config"));
    final boolean openGui = parsingResult.getBoolean("gui");
    final Path workingDirectory = Paths.get(parsingResult.getString("directory"));
    final File workingDirectoryFile = workingDirectory.toFile();
    if (!workingDirectoryFile.exists() || !workingDirectoryFile.isDirectory()) {
      LOG.fatal("Directory path should lead to an existed directory");
      parser.printHelp();
      return;
    }

    ClientStateImpl state;
    final ObjectMapper mapper = new ObjectMapper();
    if (configPath.toFile().exists()) {
      try {
        state = mapper.readValue(configPath.toFile(), ClientStateImpl.class);
      } catch (IOException e) {
        LOG.fatal("Parsing of configuration file failed. " + e.toString());
        parser.printHelp();
        return;
      }
    } else {
      LOG.info("Start with empty files information");
      state = new ClientStateImpl(Collections.emptyMap());
      try {
        mapper.writeValue(configPath.toFile(), state);
      } catch (IOException e) {
        LOG.fatal("Cannot save state to " + configPath.toAbsolutePath());
        return;
      }
    }

    try {
      final MyStateSaved listener = MyStateSaved.create(configPath);
      if (listener == null) {
        return;
      }

      state.addStateModifiedListener(listener);
    } catch (IOException e) {
      LOG.fatal("Cannot accept this configuration file: " + configPath.toAbsolutePath());
      return;
    }

    InetAddress address;
    try {
      address = InetAddress.getByName(serverAddress);
    } catch (UnknownHostException e) {
      LOG.error("Server with this address not found: " + serverAddress);
      LOG.fatal(e);
      parser.printHelp();
      return;
    }

    final UpdateServerInfoTask updateTask = new UpdateServerInfoTask(state, address, serverPort, clientPort);

    DownloadManager downloader = new DownloadManager(state, address, serverPort, workingDirectory, updateTask);
    state.getIds().forEach(downloader::startDownloadAsync);

    final TorrentClientServer client = new TorrentClientServer(clientPort, state);
    if (openGui) {
      final MainWindow mainWindow = new MainWindow(downloader, new ServerImpl(address, serverPort),
          state, updateTask);
      mainWindow.setVisible(true);
    } else {
      ReadEvalPrintLoop interfaceLoop = new ReadEvalPrintLoop(address, serverPort, state, downloader);
      interfaceLoop.addExitListener(downloader);
      interfaceLoop.addExitListener(updateTask);
      new Thread(interfaceLoop).start();
      interfaceLoop.addExitListener(() -> {
        try {
          client.shutdown();
        } catch (IOException e) {
          LOG.error("Cannot stop the server", e);
          throw new RuntimeException("Cannot stop the server");
        }
      });
    }
    client.start();
  }

  private static ArgumentParser createParser() {
    final ArgumentParser parser = ArgumentParsers.newArgumentParser("client")
        .description("Torrent client")
        .defaultHelp(true);

    parser.addArgument("-a", "--address")
        .type(String.class)
        .setDefault("localhost")
        .help("port for listening");

    parser.addArgument("-p", "--port")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_SERVER_PORT)
        .help("server port for connection");

    parser.addArgument("-l", "--listen")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_CLIENT_PORT)
        .help("port for listening");

    parser.addArgument("-c", "--config")
        .type(String.class)
        .setDefault("./files-client.json")
        .help("path to json configuration file");

    parser.addArgument("-d", "--directory")
        .type(String.class)
        .setDefault(".")
        .help("path to working directory");

    parser.addArgument("--gui")
        .action(Arguments.storeTrue())
        .type(Boolean.class)
        .help("Open the graphic user interface");

    return parser;
  }

  private static class MyStateSaved implements StateChangedListener {
    private static final ObjectMapper myMapper = new ObjectMapper();
    private final File myFile;

    private static MyStateSaved create(@NotNull Path path) throws IOException {
      final File file = path.toFile();
      if (file.exists() && (!file.isFile() || !file.canRead() || !file.canWrite())) {
        LOG.fatal("Cannot save state to file " + path.toAbsolutePath() +
            ". File must be is regular and available for reading and writing");
        return null;
      }

      //noinspection ResultOfMethodCallIgnored
      file.createNewFile();

      if (!file.exists()) {
        LOG.fatal("Cannot create new configuration file: " + path.toAbsolutePath());
        return null;
      }

      return new MyStateSaved(file);
    }

    private MyStateSaved(@NotNull File file) {
      myFile = file;
    }

    @Override
    public void stateModified(@NotNull ClientState state) {
      try {
        myMapper.writerWithDefaultPrettyPrinter().writeValue(myFile, state);
      } catch (IOException e) {
        LOG.error("Cannot save state of client.", e);
      }
    }
  }
}
