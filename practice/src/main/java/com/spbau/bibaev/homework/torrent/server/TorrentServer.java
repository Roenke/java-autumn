package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.server.handler.*;
import com.spbau.bibaev.homework.torrent.server.state.FilesChangedListener;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateImpl;
import com.spbau.bibaev.homework.torrent.server.state.SharedFiles;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TorrentServer {
  private static final Logger LOG = LogManager.getLogger(TorrentServer.class);
  private final int myPort;
  private final ServerStateEx myState;
  private static final Map<Byte, RequestHandler> myCommandId2HandlerMap;

  static {
    Map<Byte, RequestHandler> handlers = new HashMap<>();
    handlers.put(Details.Server.LIST_REQUEST_ID, new ListHandler());
    handlers.put(Details.Server.UPLOAD_REQUEST_ID, new UploadHandler());
    handlers.put(Details.Server.SOURCES_REQUEST_ID, new SourcesHandler());
    handlers.put(Details.Server.UPDATE_REQUEST_ID, new UpdateHandler());

    myCommandId2HandlerMap = Collections.unmodifiableMap(handlers);
  }

  public static void main(String[] args) {
    ArgumentParser parser = createParser();
    Namespace parsingResult;
    try {
      parsingResult = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      return;
    }

    Integer port = parsingResult.getInt("port");
    String config = parsingResult.getString("config");
    Path path = Paths.get(config);
    SharedFiles storage;
    if (path.toFile().exists()) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        LOG.info("Loading files information from: " + path.toAbsolutePath().toString());
        storage = mapper.readValue(path.toFile(), SharedFiles.class);
      } catch (IOException e) {
        LOG.error("Configuration file parsing failed. " + e.toString());
        parser.printHelp();
        return;
      }
    } else {
      LOG.info("Start with empty files information");
      storage = new SharedFiles(Collections.emptyMap());
    }

    try {
      File file = path.toFile();
      if (file.exists() && (!file.isFile() || !file.canRead() || !file.canWrite())) {
        LOG.fatal("Path to configuration file should lead to regular file with read/write access");
        return;
      }
      if (!path.toFile().exists() && !path.toFile().createNewFile()) {
        LOG.fatal("Cannot create file for save state");
        return;
      }

      final ObjectMapper mapper = new ObjectMapper();
      storage.addStateChangedListener(state -> {
        LOG.info("shared files changed");
        try {
          mapper.writeValue(file, state);
        } catch (IOException e) {
          LOG.error("cannot save state of shared file", e);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    ServerStateEx state = new ServerStateImpl(storage);

    TorrentServer server = new TorrentServer(port, state);
    try {
      server.run();
    } catch (IOException e) {
      LOG.fatal("Something went wrong", e);
    }
  }

  private TorrentServer(int port, @NotNull ServerStateEx state) {
    myPort = port;
    myState = state;
  }

  private void run() throws IOException {
    LOG.info("Starting the torrent server on " + myPort + " port");
    final ExecutorService requestsThreadPool = Executors.newFixedThreadPool(Details.Server.REQUEST_HANDLING_WORKERS);
    final ScheduledExecutorService actualClientTask = Executors.newScheduledThreadPool(1);
    actualClientTask.execute(new ConnectedClientsRefresher(myState, actualClientTask));
    try (ServerSocket socket = new ServerSocket(Details.DEFAULT_SERVER_PORT)) {
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();

        byte commandId = (byte) clientSocket.getInputStream().read();

        if (!myCommandId2HandlerMap.containsKey(commandId)) {
          LOG.warn("Unknown request received. Id = " + commandId);
        } else {
          LOG.info("Request received. Id = " + commandId);
          final RequestHandler requestHandler = myCommandId2HandlerMap.get(commandId);
          requestsThreadPool.execute(() -> requestHandler.handle(clientSocket, myState));
        }
      }
    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("server")
        .description("Torrent tracker server")
        .defaultHelp(true);

    parser.addArgument("-p", "--port")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_SERVER_PORT)
        .help("port for listening");

    parser.addArgument("-c", "--config")
        .type(String.class)
        .setDefault("./files.json")
        .help("path to json configuration file");

    return parser;
  }
}
