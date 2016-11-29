package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

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
        System.out.println("Connection received");
        new Thread(new Handler(clientSocket)).start();
      }
    } catch (IOException e) {
      System.err.println("Something wrong in separate thread server: " + e);
    }
  }

  private static class Handler implements Runnable {
    final Socket mySocket;

    Handler(Socket socket) {
      mySocket = socket;
    }

    @Override
    public void run() {
      try (InputStream is = mySocket.getInputStream(); OutputStream os = mySocket.getOutputStream()) {
        while (!mySocket.isClosed()) {
          System.out.println("try to receive data");
          int[] array = DataUtils.readArray(is);
          System.out.println("array received:" + Arrays.toString(array));
          InsertionSorter.sort(array);
          System.out.println("array sorted:" + Arrays.toString(array));
          DataUtils.write(array, os);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
