package com.spbau.bibaev.benchmark.client.runner;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Vitaliy.Bibaev
 */
public class BenchmarkRunner {
  private final BenchmarkParameters myParameters;
  private final InetAddress myServerAddress;
  private final ServerArchitectureDescription myDescription;

  public BenchmarkRunner(@NotNull BenchmarkParameters parameters, @NotNull InetAddress serverAddress,
                         @NotNull ServerArchitectureDescription description) {
    super();
    myParameters = parameters;
    myServerAddress = serverAddress;
    myDescription = description;
  }

  public long start() throws Exception {
    final int clientsCount = myParameters.getClientCount();
    final CyclicBarrier barrier = new CyclicBarrier(clientsCount + 1);
    final Thread[] clients = new Thread[clientsCount];

    AtomicLong averageTimePerClient = new AtomicLong(0);
    for (int i = 0; i < clientsCount; i++) {
      final BenchmarkClient client = BenchmarkClient.create(myParameters, myDescription, myServerAddress);
      final Thread clientThread = new Thread(() -> {
        try {
          barrier.await();
          final long clientStartTime = System.nanoTime();
          client.start();
          long duration = System.nanoTime() - clientStartTime;
          averageTimePerClient.addAndGet(duration / clientsCount);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      clientThread.start();
      clients[i] = clientThread;
    }

    barrier.await();
    for (Thread thread : clients) {
      thread.join();
    }

    return averageTimePerClient.get();
  }
}
