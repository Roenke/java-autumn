package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class PermanentConnectionCachedServer implements Runnable {
  private static final int PORT = Details.TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL;

  private final ExecutorService myThreadPool = Executors.newCachedThreadPool();

  @Override
  public void run() {
    try {
      ServerSocket socket = new ServerSocket(PORT);
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        myThreadPool.execute(new ConnectionHandler(clientSocket));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
