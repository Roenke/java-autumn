package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

public interface Branch {
  Commit getCommit();

  void moveTo(@NotNull Commit commit);

  String getName();
}
