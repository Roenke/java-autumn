package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleThreadBlockedServer extends StreamServer {
  private volatile ServerSocket mySocket;

  public SingleThreadBlockedServer(int port) {
    super(port);
  }

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(myPort, Integer.MAX_VALUE)) {
      mySocket = socket;
      while (!socket.isClosed()) {
        try (final Socket clientSocket = socket.accept();
             final InputStream is = clientSocket.getInputStream();
             final OutputStream os = clientSocket.getOutputStream()) {
          handle(is, os);
        }
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
