package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class PermanentConnectionCachedServer extends TcpServer {
  private static final int PORT = Details.TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL;

  private volatile ServerSocket mySocket;
  private final ExecutorService myThreadPool = Executors.newCachedThreadPool();

  @Override
  public void start() throws IOException {
    try (ServerSocket socket = new ServerSocket(PORT)) {
      mySocket = socket;
      while (!socket.isClosed()) {
        final Socket clientSocket = socket.accept();
        myThreadPool.execute(new ConnectionHandler(clientSocket));
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
