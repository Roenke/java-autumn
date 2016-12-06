package com.spbau.bibaev.benchmark.server.arch.tcp;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServerTest extends TcpServerTest {
  @Test
  public void serverShouldDoNotCloseConnection()
      throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }

  @Override
  public TcpServer getServer(int port) {
    return new SeparateThreadServer(port);
  }
}
