package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.server.arch.tcp.NonblockingServer;
import com.spbau.bibaev.benchmark.server.arch.tcp.PermanentConnectionCachedServer;
import com.spbau.bibaev.benchmark.server.arch.tcp.SeparateThreadServer;
import com.spbau.bibaev.benchmark.server.arch.tcp.SingleThreadBlockedServer;
import com.spbau.bibaev.benchmark.server.arch.udp.FixedThreadPoolServer;
import com.spbau.bibaev.benchmark.server.arch.udp.NewThreadProcessingServer;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerEntryPoint {
  public static void main(String[] args) {
    new Thread(new SeparateThreadServer(Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT)).start();
    new Thread(new SingleThreadBlockedServer(Details.TcpPorts.NEW_CONNECTION_SINGLE_THREADED)).start();
    new Thread(new PermanentConnectionCachedServer(Details.TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL)).start();
    new Thread(new NonblockingServer(Details.TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING)).start();

    new Thread(new FixedThreadPoolServer()).start();
    new Thread(new NewThreadProcessingServer()).start();
  }
}
