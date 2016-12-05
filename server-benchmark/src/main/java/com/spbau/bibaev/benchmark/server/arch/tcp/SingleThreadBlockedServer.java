package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleThreadBlockedServer implements Runnable {
  private static final int PORT = Details.TcpPorts.NEW_CONNECTION_SINGLE_THREADED;

  @Override
  public void run() {
    try {
      ServerSocket socket = new ServerSocket(PORT);
      while (!socket.isClosed()) {
        try (Socket clientSocket = socket.accept()) {
          try (InputStream is = clientSocket.getInputStream();
               OutputStream os = clientSocket.getOutputStream()) {
            final int[] array = DataUtils.readArray(is);
            InsertionSorter.sort(array);
            DataUtils.write(array, os);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
