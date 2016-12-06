package com.spbau.bibaev.benchmark.client;

import com.spbau.bibaev.benchmark.client.runner.BenchmarkRunner;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;

import java.net.InetAddress;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleBenchmarkRunner {
  private static final int ITERATIONS_COUNT = 1000;

  public static void main(String[] args) {
    final List<ServerArchitectureDescription> serverArchitectureDescriptions = Details.availableArchitectures();

    try {
      final int archIndex = Integer.parseInt(args[0]);
      final InetAddress address = InetAddress.getByName(args[1]);
      final int n = Integer.parseInt(args[2]);
      final int m = Integer.parseInt(args[3]);
      final int delta = Integer.parseInt(args[4]);
      final int x = Integer.parseInt(args[5]);
      final ServerArchitectureDescription arch = serverArchitectureDescriptions.get(archIndex);
      final BenchmarkParameters parameters = new BenchmarkParameters(n, m, x, delta);
      final BenchmarkRunner runner = new BenchmarkRunner(parameters, address, arch);

      long minimumDuration = Long.MAX_VALUE;
      for (int i = 0; i < ITERATIONS_COUNT; i++) {
        final long duration = runner.start();
        minimumDuration = Math.min(minimumDuration, duration);
      }

      System.out.println(minimumDuration);
    } catch (Throwable e) {
      System.out.println(e.toString());
      usage();
    }

  }

  private static void usage() {
    final List<ServerArchitectureDescription> serverArchitectureDescriptions = Details.availableArchitectures();

    System.out.println("Usage:");
    System.out.println("\tjava " + SingleBenchmarkRunner.class.getSimpleName() + " arch-num address N M Delta X");
    System.out.println("Where: ");
    System.out.println("\tarch-num - architecture for benchmark. One of the following numbers:");
    for (int i = 0; i < serverArchitectureDescriptions.size(); i++) {
      final ServerArchitectureDescription arch = serverArchitectureDescriptions.get(i);
      System.out.println("\t\t" + i + " - " + arch.getName());
    }
    System.out.println("\t N -size of random array");
    System.out.println("\t M - count of clients");
    System.out.println("\t Delta - delay between two requests");
    System.out.println("\t X - iteration count for each client");
  }
}
