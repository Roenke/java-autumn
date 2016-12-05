package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.Details;

/**
 * @author Vitaliy.Bibaev
 */
public class NewThreadServerTest extends BaseUdpServerTest {
  @Override
  protected int getPort() {
    return Details.UdpPorts.THREAD_PER_REQUEST;
  }

  @Override
  protected Runnable getServer() {
    return new NewThreadProcessingServer();
  }
}
