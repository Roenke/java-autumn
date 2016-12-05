package com.spbau.bibaev.benchmark.client.runner;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.Random;

/**
 * @author Vitaliy.Bibaev
 */
public class UdpBenchmarkClient extends BenchmarkClient {
  private final int[] myData;
  private final int myDelay;
  private final int myIterationCount;
  private final InetAddress myServerAddress;
  private final int myServerPort;
  UdpBenchmarkClient(int dataSize, int delayMs, int iterationCount,
                     @NotNull InetAddress serverAddress, int serverPort) {
    myData = new Random().ints().limit(dataSize).toArray();
    myDelay = delayMs;
    myIterationCount = iterationCount;
    myServerAddress = serverAddress;
    myServerPort = serverPort;
  }

  @Override
  public void start() throws Exception {
    // TODO
  }
}
