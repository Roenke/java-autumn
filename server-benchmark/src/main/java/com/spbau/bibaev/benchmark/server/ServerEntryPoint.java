package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.server.arch.tcp.PermanentConnectionCachedServer;
import com.spbau.bibaev.benchmark.server.arch.tcp.SeparateThreadServer;
import com.spbau.bibaev.benchmark.server.arch.tcp.SingleThreadBlockedServer;
import com.spbau.bibaev.benchmark.server.arch.udp.FixedThreadPoolServer;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerEntryPoint {
  public static void main(String[] args) {
    new Thread(new SeparateThreadServer()).start();
    new Thread(new SingleThreadBlockedServer()).start();
    new Thread(new PermanentConnectionCachedServer()).start();

    new Thread(new FixedThreadPoolServer()).start();
  }
}
