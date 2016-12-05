package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.Details;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author Vitaliy.Bibaev
 */
public class CachedThreadPoolServerTest extends TcpServersTest {
  @Override
  public int getPort() {
    return Details.TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL;
  }

  @Override
  public TcpServer getServer() {
    return new PermanentConnectionCachedServer();
  }

  @Test
  public void serverShouldDoNotCloseConnection() throws BrokenBarrierException, InterruptedException, IOException {
    permanentConnectionTest();
  }
}
