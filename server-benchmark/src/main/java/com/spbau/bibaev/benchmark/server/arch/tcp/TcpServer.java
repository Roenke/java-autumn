package com.spbau.bibaev.benchmark.server.arch.tcp;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author Vitaliy.Bibaev
 */
abstract class TcpServer implements Runnable {
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

  abstract void shutdown() throws IOException;
}
