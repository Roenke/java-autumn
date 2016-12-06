package com.spbau.bibaev.benchmark.server.arch.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServer extends TcpServer {
  private volatile ServerSocket mySocket;

  public SeparateThreadServer(int port) {
    super(port);
  }

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(myPort)) {
      mySocket = socket;
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        new Thread(new ConnectionHandler(clientSocket)).start();
      }
    }
  }

  @Override
  public void shutdown() throws IOException {
    if (mySocket != null) {
      mySocket.close();
    }
  }
}
