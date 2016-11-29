package com.spbau.bibaev.benchmark.client;

import org.jetbrains.annotations.NotNull;

/**
 * @author Vitaliy.Bibaev
 */
public interface Log {
  void log(@NotNull String message);
  void log(int clientNumber, @NotNull String message);
}
