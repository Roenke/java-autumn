package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class NonblockingServerTest extends TcpServerTest {
  @Override
  public int getPort() {
    return Details.TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING;
  }

  @Override
  public TcpServer getServer() {
    return new NonblockingServer();
  }

  @Test
  public void holdConnectionTest() throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }
}
