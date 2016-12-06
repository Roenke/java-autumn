package com.spbau.bibaev.benchmark.server.arch.tcp;

import java.io.IOException;

/**
 * @author Vitaliy.Bibaev
 */
public class AsyncServer extends TcpServer {
  public AsyncServer(int port) {
    super(port);
  }

  @Override
  void start() throws IOException {

  }

  @Override
  void shutdown() throws IOException {

  }
}
