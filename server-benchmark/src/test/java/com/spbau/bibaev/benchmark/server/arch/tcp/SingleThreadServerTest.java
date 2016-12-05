package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleThreadServerTest extends TcpServersTest {
  @Override
  public int getPort() {
    return Details.TcpPorts.NEW_CONNECTION_SINGLE_THREADED;
  }

  @Override
  public Runnable getServer() {
    return new SingleThreadBlockedServer();
  }
}
