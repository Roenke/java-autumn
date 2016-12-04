package com.spbau.bibaev.homework.torrent.client;

import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.handler.GetHandler;
import com.spbau.bibaev.homework.torrent.client.handler.StatHandler;
import com.spbau.bibaev.homework.torrent.common.AbstractRequestHandler;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TorrentClientServer {
  private static final Logger LOG = LogManager.getLogger(TorrentClientServer.class);

  private static final Map<Byte, AbstractRequestHandler<ClientState>> REQUEST_HANDLERS;

  private final ClientStateEx myState;
  private final int myClientPort;
  private volatile ServerSocket myServerSocket;

  static {
    Map<Byte, AbstractRequestHandler<ClientState>> handlers = new HashMap<>();
    handlers.put(Details.Client.GET_REQUEST_ID, new GetHandler());
    handlers.put(Details.Client.STAT_REQUEST_ID, new StatHandler());

    REQUEST_HANDLERS = Collections.unmodifiableMap(handlers);
  }

  public TorrentClientServer(int clientPort, @NotNull ClientStateEx state) {
    myState = state;
    myClientPort = clientPort;
  }

  public void shutdown() throws IOException {
    myServerSocket.close();
  }

  public void start() {
    final ExecutorService requestHandlingThreadPool = Executors
        .newFixedThreadPool(Details.Client.REQUEST_HANDLING_WORKERS);
    try (ServerSocket serverSocket = new ServerSocket(myClientPort)) {
      myServerSocket = serverSocket;
      while (!myServerSocket.isClosed()) {
        Socket socket = serverSocket.accept();
        requestHandlingThreadPool.execute(() -> {
          try (Socket anotherClient = socket;
               DataInputStream is = new DataInputStream(anotherClient.getInputStream())) {
            final byte commandId = is.readByte();
            if (!REQUEST_HANDLERS.containsKey(commandId)) {
              LOG.warn("Unknown request received. Id = " + commandId);
            } else {
              LOG.info("Start handling request with id = " + commandId);
              REQUEST_HANDLERS.get(commandId).handle(socket, myState);
              LOG.info("Request with id = " + commandId + " handled");
            }
          } catch (IOException e) {
            LOG.error("Something went wrong in request handling process", e);
          }
        });

      }
    } catch (IOException e) {
      LOG.fatal("Something went wrong.", e);
    }
  }
}
