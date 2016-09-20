package com.spbau.bibaev.homework.vcs.ex;

import org.jetbrains.annotations.NotNull;

public class RepositoryIllegalStateException extends RepositoryException {
  public RepositoryIllegalStateException(@NotNull String message) {
    super(message);
  }

  public RepositoryIllegalStateException(@NotNull String message, @NotNull Throwable e) {
    super(message, e);
  }
}
