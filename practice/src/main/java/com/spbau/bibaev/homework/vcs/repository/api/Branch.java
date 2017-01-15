package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

public interface Branch {
  @NotNull
  Commit getCommit();

  @NotNull
  String getName();
}
