package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.server.arch.ServerWithStatistics;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author Vitaliy.Bibaev
 */
abstract class TcpServer extends ServerWithStatistics {
  final int myPort;

  public TcpServer(int port) {
    myPort = port;
  }

  @Override
  public final void run() {
    try {
      start();
    } catch (SocketException e) {
      // usual case: server socket was closed
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  abstract void start() throws IOException;
}
