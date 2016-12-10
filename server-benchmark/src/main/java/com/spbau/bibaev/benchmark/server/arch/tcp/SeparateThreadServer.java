package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServer extends StreamServer {
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
        new Thread(() -> {
          try (InputStream is = clientSocket.getInputStream(); OutputStream os = clientSocket.getOutputStream()) {
            while (!clientSocket.isClosed()) {
              handle(is, os);
            }
          } catch (EOFException ignored) {
            // an usual case.
          } catch (IOException e) {
            e.printStackTrace();
          }
        }).start();
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
