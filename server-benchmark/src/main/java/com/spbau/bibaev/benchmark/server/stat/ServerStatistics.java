package com.spbau.bibaev.benchmark.server.stat;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerStatistics {
  private static final int MAX_STARS_PER_LINE = 100;

  private final List<Long> myQueryProcessingTimes = Collections.synchronizedList(new ArrayList<>());
  private final List<Long> myClientProcessingTimes = Collections.synchronizedList(new ArrayList<>());

  public void pushStatistics(long clientProcessingTime, long queryProcessingTime) {
    myQueryProcessingTimes.add(queryProcessingTime);
    myClientProcessingTimes.add(clientProcessingTime);
  }

  public long getQueryProcessingMetric() {
    return Collections.min(myQueryProcessingTimes);
  }

  public long getClientProcessingMetric() {
    return Collections.min(myClientProcessingTimes);
  }

  public void printClientProcessingDistribution(@NotNull PrintStream stream, double stepSize) {
    printDistribution(myClientProcessingTimes, stream, stepSize);
  }

  public void printQueryProcessingDistribution(@NotNull PrintStream stream, double stepSize) {
    printDistribution(myQueryProcessingTimes, stream, stepSize);
  }

  private void printDistribution(@NotNull List<Long> results, @NotNull PrintStream printer, double stepSize) {
    int size = results.size();
    long min = Collections.min(results);
    long max = Collections.max(results);
    long avg = results.stream().mapToLong(Long::longValue).sum() / size;

    printer.printf("min = %d%n", min);
    printer.printf("max = %d%n", max);
    printer.printf("abg = %d%n", avg);

    long step = (long) (min * stepSize);
    Map<Long, Integer> from2Count = new HashMap<>();
    for (long value : results) {
      long basketNumber = (value - min) / step;
      long from = basketNumber * step + min;
      from2Count.put(from, 1 + from2Count.getOrDefault(from, 0));
    }

    from2Count.forEach((from, count) -> {
      int stars = (int) (MAX_STARS_PER_LINE * ((double) count / size));
      printer.printf("%d ns : ", from);
      printer.print(new String(new char[stars]).replace('\0', '*'));
      printer.println();
    });
  }
}
