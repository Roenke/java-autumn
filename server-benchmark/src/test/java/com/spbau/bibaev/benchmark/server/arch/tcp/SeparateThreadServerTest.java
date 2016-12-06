package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class SeparateThreadServerTest extends TcpServerTest {
  @Override
  public int getPort() {
    return Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT;
  }

  @Override
  public TcpServer getServer() {
    return new SeparateThreadServer();
  }

  @Test
  public void serverShouldDoNotCloseConnection()
      throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }
}
