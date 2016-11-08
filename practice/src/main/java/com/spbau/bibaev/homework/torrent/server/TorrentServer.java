package com.spbau.bibaev.homework.torrent.server;

import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.server.handler.*;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
  private volatile ServerSocket mySocket;

  static {
    Map<Byte, RequestHandler> handlers = new HashMap<>();
    handlers.put(Details.Server.LIST_REQUEST_ID, new ListHandler());
    handlers.put(Details.Server.UPLOAD_REQUEST_ID, new UploadHandler());
    handlers.put(Details.Server.SOURCES_REQUEST_ID, new SourcesHandler());
    handlers.put(Details.Server.UPDATE_REQUEST_ID, new UpdateHandler());

    myCommandId2HandlerMap = Collections.unmodifiableMap(handlers);
  }

  public TorrentServer(int port, @NotNull ServerStateEx state) {
    myPort = port;
    myState = state;
  }

  public void shutdown() throws IOException {
    mySocket.close();
  }

  public boolean isActive() {
    return mySocket != null && !mySocket.isClosed();
  }

  public void start() throws IOException {
    LOG.info("Starting the torrent server on " + myPort + " port");
    final ExecutorService requestsThreadPool = Executors.newFixedThreadPool(Details.Server.REQUEST_HANDLING_WORKERS);
    final ScheduledExecutorService actualClientTask = Executors.newScheduledThreadPool(1);
    actualClientTask.execute(new ConnectedClientsRefresher(myState, actualClientTask));

    try (ServerSocket socket = new ServerSocket(myPort)) {
      mySocket = socket;
      LOG.info("Server started on port " + myPort);
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        LOG.info("New connection received!");
        requestsThreadPool.execute(() -> {
          try (Socket client = clientSocket;
               InputStream is = client.getInputStream()) {
            byte commandId = (byte) is.read();
            LOG.info("New request received id = " + commandId);
            if (!myCommandId2HandlerMap.containsKey(commandId)) {
              LOG.warn("Unknown request received. Id = " + commandId);
            } else {
              LOG.info("Request received. Id = " + commandId);

              final RequestHandler requestHandler = myCommandId2HandlerMap.get(commandId);
              requestHandler.handle(client, myState);
              LOG.info("Request handled. Id = " + commandId);
            }
          } catch (IOException e) {
            LOG.error("Something went wrong: ", e);
          }
        });
      }
    }
  }
}

