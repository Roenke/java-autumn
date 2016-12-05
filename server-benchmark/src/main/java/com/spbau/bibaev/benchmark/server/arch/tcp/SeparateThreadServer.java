package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServer extends TcpServer {
  private static final int PORT = Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT;
  private volatile ServerSocket mySocket;

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(PORT)) {
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
