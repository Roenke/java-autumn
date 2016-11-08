package com.spbau.bibaev.homework.torrent.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.handler.GetHandler;
import com.spbau.bibaev.homework.torrent.client.handler.StatHandler;
import com.spbau.bibaev.homework.torrent.client.impl.ClientStateImpl;
import com.spbau.bibaev.homework.torrent.client.repl.ReadEvalPrintLoopWorker;
import com.spbau.bibaev.homework.torrent.common.AbstractRequestHandler;
import com.spbau.bibaev.homework.torrent.common.Details;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TorrentClient {
  private static final Logger LOG = LogManager.getLogger(TorrentClient.class);

  private static final Map<Byte, AbstractRequestHandler<ClientState>> REQUEST_HANDLERS;

  private final InetAddress myServerAddress;
  private final int myServerPort;
  private final int myClientPort;
  private final ClientStateEx myState;

  private volatile boolean myIsCancelled;

  static {
    Map<Byte, AbstractRequestHandler<ClientState>> handlers = new HashMap<>();
    handlers.put(Details.Client.GET_REQUEST_ID, new GetHandler());
    handlers.put(Details.Client.STAT_REQUEST_ID, new StatHandler());

    REQUEST_HANDLERS = Collections.unmodifiableMap(handlers);
  }

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

    ClientStateEx state;
    if (configPath.toFile().exists()) {
      final ObjectMapper mapper = new ObjectMapper();
      try {
        state = mapper.readValue(configPath.toFile(), ClientStateImpl.class);
      } catch (IOException e) {
        LOG.error("Parsing of configuration file failed. " + e.toString());
        parser.printHelp();
        return;
      }
    } else {
      LOG.info("Start with empty files information");
      state = new ClientStateImpl(Collections.emptyMap());
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

    TorrentClient client = new TorrentClient(clientPort, serverPort, address, state);

    try {
      client.run();
    } catch (IOException e) {
      LOG.fatal("Something went wrong", e);
    }
  }

  private TorrentClient(int listenPort, int serverPort, @NotNull InetAddress serverAddress,
                        @NotNull ClientStateEx state) {
    myClientPort = listenPort;
    myServerPort = serverPort;
    myServerAddress = serverAddress;
    myState = state;
  }

  public void run() throws IOException {
    final UpdateServerInfoTask updateTask = new UpdateServerInfoTask(myState, myServerAddress,
        myServerPort, myClientPort);
    updateTask.startAsync();

    DownloadManager downloader = new DownloadManager(myState, myServerAddress, myServerPort);
    myState.getIds().forEach(downloader::startDownloadAsync);
    final Collection<Integer> filesInProcess = downloader.getFilesInProcess();
    if (filesInProcess.isEmpty()) {
      LOG.info("All files are loaded");
    } else {
      filesInProcess.forEach(x -> LOG.info("Start loading for file with id = " + x));
    }

    ReadEvalPrintLoopWorker repl = new ReadEvalPrintLoopWorker(myServerAddress, myServerPort, myState, downloader);
    repl.addExitListener(() -> myIsCancelled = true);
    new Thread(repl).start();

    final ExecutorService requestHandlingThreadPool = Executors
        .newFixedThreadPool(Details.Client.REQUEST_HANDLING_WORKERS);
    try (ServerSocket serverSocket = new ServerSocket(myClientPort)) {
      while (!myIsCancelled) {
        final Socket socket = serverSocket.accept();
        try (InputStream is = socket.getInputStream()) {
          byte requestId = (byte) is.read();
          if (!REQUEST_HANDLERS.containsKey(requestId)) {
            LOG.info("Unknown request with id = " + requestId);
          } else {
            requestHandlingThreadPool.execute(() -> REQUEST_HANDLERS.get(requestId).handle(socket, myState));
          }
        }
      }

    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("client")
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
        .setDefault("./files.json")
        .help("path to json configuration file");

    return parser;
  }
}
