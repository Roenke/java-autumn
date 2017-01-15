package com.spbau.bibaev.benchmark.server.stat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerStatistics {
  private final List<Long> myQueryProcessingTimes = Collections.synchronizedList(new ArrayList<>());
  private final List<Long> myClientProcessingTimes = Collections.synchronizedList(new ArrayList<>());

  public void pushStatistics(long clientProcessingTime, long queryProcessingTime) {
    myQueryProcessingTimes.add(queryProcessingTime);
    myClientProcessingTimes.add(clientProcessingTime);
  }

  public long getQueryProcessingMetric() {
    return average(myQueryProcessingTimes);
  }

  public void clear() {
    myClientProcessingTimes.clear();
    myQueryProcessingTimes.clear();
  }

  public long getClientProcessingMetric() {
    return average(myClientProcessingTimes);
  }

  private long average(@NotNull List<Long> longs) {
    int size = longs.size();
    if (size == 0) {
      return Long.MAX_VALUE;
    }

    long sum = 0;

    //noinspection ForLoopReplaceableByForEach
    for (int i = 0; i < size; i++) {
      sum += longs.get(i);
    }

    return sum / size;
  }
}
