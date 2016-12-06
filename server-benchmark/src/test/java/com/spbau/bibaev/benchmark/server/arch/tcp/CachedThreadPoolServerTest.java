package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class CachedThreadPoolServerTest extends TcpServerTest {
  @Test
  public void serverShouldDoNotCloseConnection() throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }

  @Override
  public TcpServer getServer(int port) {
    return new PermanentConnectionCachedServer(port);
  }
}
