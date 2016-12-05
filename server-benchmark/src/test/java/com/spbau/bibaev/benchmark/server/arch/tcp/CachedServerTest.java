package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;

/**
 * @author Vitaliy.Bibaev
 */
public class CachedServerTest extends TcpServersTest {
  @Override
  public int getPort() {
    return Details.TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL;
  }

  @Override
  public Runnable getServer() {
    return new PermanentConnectionCachedServer();
  }
}
