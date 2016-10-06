package com.spbau.bibaev.homework.vcs.ex;

import org.jetbrains.annotations.NotNull;

public class RepositoryException extends RuntimeException {
  public RepositoryException(@NotNull String message) {
    super(message);
  }

  public RepositoryException(@NotNull String message, @NotNull Throwable e) {
    super(message, e);
  }
}
