package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.Details;

/**
 * @author Vitaliy.Bibaev
 */
public class FixedThreadPoolTest extends BaseUdpServerTest {
  @Override
  protected int getPort() {
    return Details.UdpPorts.FIXED_THREAD_POOL;
  }

  @Override
  protected Runnable getServer() {
    return new FixedThreadPoolServer();
  }
}
