package com.spbau.bibaev.benchmark.server.arch.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class PermanentConnectionCachedServer extends TcpServer {
  private volatile ServerSocket mySocket;
  private final ExecutorService myThreadPool = Executors.newCachedThreadPool();

  public PermanentConnectionCachedServer(int port) {
    super(port);
  }

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(myPort)) {
      mySocket = socket;
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        myThreadPool.execute(new ConnectionHandler(clientSocket));
      }
    }
  }

  @Override
  public void shutdown() throws IOException {
    if (mySocket != null) {
      mySocket.close();
    }

    myThreadPool.shutdown();
  }
}
