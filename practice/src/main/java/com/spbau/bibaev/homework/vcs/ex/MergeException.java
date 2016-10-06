package com.spbau.bibaev.homework.vcs.ex;

import org.jetbrains.annotations.NotNull;

public class MergeException extends RepositoryException {
  public MergeException(@NotNull String message, @NotNull Throwable e) {
    super(message, e);
  }

  public MergeException(@NotNull String message) {
    super(message);
  }
}
