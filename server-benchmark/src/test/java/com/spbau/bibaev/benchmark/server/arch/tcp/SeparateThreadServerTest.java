package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServerTest extends TcpServersTest {
  @Override
  public int getPort() {
    return Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT;
  }

  @Override
  public Runnable getServer() {
    return new SeparateThreadServer();
  }
}
