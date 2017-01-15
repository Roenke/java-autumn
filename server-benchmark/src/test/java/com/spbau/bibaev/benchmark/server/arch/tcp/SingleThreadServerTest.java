package com.spbau.bibaev.benchmark.server.arch.tcp;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleThreadServerTest extends TcpServerTest {
  @Override
  public TcpServer getServer(int port) {
    return new SingleThreadBlockedServer(port);
  }
}
