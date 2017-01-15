package com.spbau.bibaev.benchmark.client.runner;

/**
 * @author Vitaliy.Bibaev
 */
public class BenchmarkResult {
  public final long averageClientLifeTime;
  public final long averagePerClientTime;
  public final long averagePerQueryTime;

  public BenchmarkResult(long averageClient, long averagePerClient, long averagePerQuery) {
    averageClientLifeTime = averageClient;
    averagePerClientTime = averagePerClient;
    averagePerQueryTime = averagePerQuery;
  }

  @Override
  public String toString() {
    return String.format("%d \t %d \t %d", averageClientLifeTime, averagePerClientTime, averagePerQueryTime);
  }
}
