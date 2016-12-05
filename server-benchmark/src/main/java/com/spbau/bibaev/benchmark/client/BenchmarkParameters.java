package com.spbau.bibaev.benchmark.client;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author Vitaliy.Bibaev
 */
public class BenchmarkParameters {
  private final int myDataSize;
  private final int myClientCount;
  private final int myIterationCount;
  private final int myDelay;

  private BenchmarkParameters(int dataSize, int clients, int iterations, int delayMs) {
    myDataSize = dataSize;
    myClientCount = clients;
    myIterationCount = iterations;
    myDelay = delayMs;
  }

  public static Iterator<BenchmarkParameters> createIterator(
      @NotNull Iterator<Integer> dataSizeIterator,
      @NotNull Iterator<Integer> clientsCountIterator,
      @NotNull Iterator<Integer> delayIterator, int iterationCount) {
    return new Iterator<BenchmarkParameters>() {
      @Override
      public boolean hasNext() {
        return dataSizeIterator.hasNext() && clientsCountIterator.hasNext() && delayIterator.hasNext();
      }

      @Override
      public BenchmarkParameters next() {
        return new BenchmarkParameters(dataSizeIterator.next(), clientsCountIterator.next(),
            iterationCount, delayIterator.next());
      }
    };
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
