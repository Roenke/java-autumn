package com.spbau.bibaev.homework.vcs.ex;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class RepositoryIOException extends RepositoryException {
  public RepositoryIOException(@NotNull String message) {
    super(message);
  }

  public RepositoryIOException(@NotNull String message, @Nullable Exception parentException) {
    super(message, parentException);
  }
}
