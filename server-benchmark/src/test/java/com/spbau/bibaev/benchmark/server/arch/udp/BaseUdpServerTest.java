package com.spbau.bibaev.benchmark.server.arch.udp;

import org.junit.Test;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class BaseUdpServerTest {

  @Test
  public void sortingTest() {

  }

  protected abstract int getPort();

  protected abstract Runnable getServer();
}
