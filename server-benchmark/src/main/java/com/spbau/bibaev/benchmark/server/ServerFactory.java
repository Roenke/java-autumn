package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.server.arch.tcp.*;
import com.spbau.bibaev.benchmark.server.arch.udp.FixedThreadPoolServer;
import com.spbau.bibaev.benchmark.server.arch.udp.NewThreadProcessingServer;
import org.jetbrains.annotations.Nullable;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerFactory {

  @Nullable
  public static Runnable getServerByDefaultPort(int port) {
    switch (port) {
      case Details.TcpPorts.ASYNC_PROCESSING:
        return new AsyncServer(port);
      case Details.TcpPorts.NEW_CONNECTION_SINGLE_THREADED:
        return new SingleThreadBlockedServer(port);
      case Details.TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL:
        return new PermanentConnectionCachedServer(port);
      case Details.TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING:
        return new NonblockingServer(port);
      case Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT:
        return new SeparateThreadServer(port);
      case Details.UdpPorts.FIXED_THREAD_POOL:
        return new FixedThreadPoolServer();
      case Details.UdpPorts.THREAD_PER_REQUEST:
        return new NewThreadProcessingServer();
      default:
        return null;
    }
  }
}
