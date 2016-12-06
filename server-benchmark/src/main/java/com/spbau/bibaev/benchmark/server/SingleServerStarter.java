package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;

import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleServerStarter {
  public static void main(String[] args) throws InterruptedException {
    final List<ServerArchitectureDescription> serverArchitectureDescriptions = Details.availableArchitectures();
    int number;
    try {
      number = args.length != 1 ? -1 : Integer.parseInt(args[0]);
    } catch (Throwable e) {
      number = -1;
    }

    if (number < 0 || number >= serverArchitectureDescriptions.size()) {
      System.out.println("Usage:");
      System.out.println("\tjava " + SingleServerStarter.class.getSimpleName() + " <arch-num>");
      System.out.println("Where arch-num - is numbers of architecture for starting. One of the following numbers:");
      for (int i = 0; i < serverArchitectureDescriptions.size(); i++) {
        final ServerArchitectureDescription arch = serverArchitectureDescriptions.get(i);
        System.out.println(i + " - " + arch.getName());
      }

      return;
    }

    final ServerArchitectureDescription arch = serverArchitectureDescriptions.get(number);
    final Thread serverThread = new Thread(ServerFactory.getServerByDefaultPort(arch.getDefaultServerPort()));
    serverThread.start();
    System.out.println("Server successfully started: " + arch.getName());
    serverThread.join();
  }
}
