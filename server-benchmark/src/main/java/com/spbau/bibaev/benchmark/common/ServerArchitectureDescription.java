package com.spbau.bibaev.benchmark.common;

import org.jetbrains.annotations.NotNull;

/**
 * @author Vitaliy.Bibaev
 */
public interface ServerArchitectureDescription {
  @NotNull
  String getName();

  @NotNull
  Protocol getProtocol();

  int getServerPort();

  boolean holdConnection();
}
