package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleThreadBlockedServer extends TcpServer {
  private volatile ServerSocket mySocket;

  public SingleThreadBlockedServer(int port) {
    super(port);
  }

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(myPort)) {
      mySocket = socket;
      while (!socket.isClosed()) {
        try (Socket clientSocket = socket.accept();
             InputStream is = clientSocket.getInputStream();
             OutputStream os = clientSocket.getOutputStream()) {
          final int[] array = DataUtils.readArray(is);
          InsertionSorter.sort(array);
          DataUtils.write(array, os);
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
