package com.spbau.bibaev.benchmark.server.arch.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class PermanentConnectionCachedServer extends StreamServer {
  private volatile ServerSocket mySocket;
  private final ExecutorService myThreadPool = Executors.newCachedThreadPool();

  public PermanentConnectionCachedServer(int port) {
    super(port);
  }

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(myPort, Integer.MAX_VALUE)) {
      mySocket = socket;
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        myThreadPool.execute(() -> {
          try (InputStream is = clientSocket.getInputStream(); OutputStream os = clientSocket.getOutputStream()) {
            while (!clientSocket.isClosed()) {
              handle(is, os);
            }
          } catch (EOFException ignored) {
            // an usual case.
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
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
