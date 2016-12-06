package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerEntryPoint {
  public static void main(String[] args) throws InterruptedException {
    final List<ServerArchitectureDescription> architectures = Details.availableArchitectures();
    final List<Thread> serverThreads = new ArrayList<>(architectures.size());

    for (ServerArchitectureDescription arch : architectures) {
      final Runnable server = ServerFactory.getServerByDefaultPort(arch.getDefaultServerPort());
      final Thread thread = new Thread(server);
      thread.start();
      serverThreads.add(thread);
    }
    for (final Thread serverThread : serverThreads) {
      serverThread.join();
    }
  }
}
