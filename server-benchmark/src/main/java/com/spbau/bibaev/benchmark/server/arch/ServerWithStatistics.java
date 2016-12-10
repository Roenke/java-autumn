package com.spbau.bibaev.benchmark.server.arch;

import com.spbau.bibaev.benchmark.server.stat.ServerStatistics;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class ServerWithStatistics implements Runnable {
  private final ServerStatistics myStatistics = new ServerStatistics();

  protected void updateStatistics(long clientProcessingTime, long queryProcessingTime) {
    myStatistics.pushStatistics(clientProcessingTime, queryProcessingTime);
  }

  @NotNull
  public ServerStatistics getStatistics() {
    return myStatistics;
  }
}
