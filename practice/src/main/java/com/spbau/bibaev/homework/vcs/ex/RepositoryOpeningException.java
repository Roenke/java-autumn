package com.spbau.bibaev.homework.vcs.ex;

import org.jetbrains.annotations.NotNull;

public class RepositoryOpeningException extends Exception {
  public RepositoryOpeningException(@NotNull String message, @NotNull Exception exception) {
    super(message, exception);
  }

  public RepositoryOpeningException(@NotNull String message) {
    super(message);
  }
}
