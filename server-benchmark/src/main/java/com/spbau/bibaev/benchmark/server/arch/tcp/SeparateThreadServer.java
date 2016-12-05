package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServer implements Runnable {
  private static final int PORT = Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT;

  @Override
  public void run() {
    try {
      ServerSocket socket = new ServerSocket(PORT);
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        new Thread(new ConnectionHandler(clientSocket)).start();
      }
    } catch (IOException e) {
      System.err.println("Something wrong in separate thread server: " + e);
    }
  }
}
