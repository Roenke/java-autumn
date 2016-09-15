package com.spbau.bibaev.homework.vcs;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Repository {

  private Repository(@NotNull File directory) {

  }

  public boolean isInitialized() {
    return false;
  }

  @NotNull
  public static Repository open(@NotNull File projectDirectory) {
    return new Repository(projectDirectory);
  }

  private class Storage {
  }
}
