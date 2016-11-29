package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.server.arch.tcp.SeparateThreadServer;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerEntryPoint {
  public static void main(String[] args) {
    new Thread(new SeparateThreadServer()).start();
  }
}
