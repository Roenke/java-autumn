package com.spbau.bibaev.benchmark.server.arch.tcp;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class AsyncServerTest extends TcpServerTest {
  @Override
  public TcpServer getServer(int port) {
    return new AsyncServer(port);
  }

  @Test
  public void serverShouldDoNotCloseConnection() throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }

  // workaround: Socket channel not unbind when it closed
  @Override
  int getPortForTesting() {
    return 51000;
  }

  @Override
  int getPortForTestingPermanentConnection() {
    return 51001;
  }
}
