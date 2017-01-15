package com.spbau.bibaev.benchmark.server.arch.tcp;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class NonblockingServerTest extends TcpServerTest {
  @Test
  public void holdConnectionTest() throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }

  @Override
  public TcpServer getServer(int port) {
    return new NonblockingServer(port);
  }

  // workaround: Socket channel not unbind when it closed
  @Override
  int getPortForTesting() {
    return 50000;
  }

  @Override
  int getPortForTestingPermanentConnection() {
    return 50001;
  }
}
