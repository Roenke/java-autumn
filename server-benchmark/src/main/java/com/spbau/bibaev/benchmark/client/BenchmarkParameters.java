package com.spbau.bibaev.benchmark.client;

/**
 * @author Vitaliy.Bibaev
 */
public class BenchmarkParameters {
  private final int myDataSize;
  private final int myClientCount;
  private final int myIterationCount;
  private final int myDelay;

  public BenchmarkParameters(int dataSize, int clients, int iterations, int delayMs) {
    myDataSize = dataSize;
    myClientCount = clients;
    myIterationCount = iterations;
    myDelay = delayMs;
  }

  public int getDataSize() {
    return myDataSize;
  }

  public int getClientCount() {
    return myClientCount;
  }

  public int getIterationCount() {
    return myIterationCount;
  }

  public int getDelay() {
    return myDelay;
  }
}
