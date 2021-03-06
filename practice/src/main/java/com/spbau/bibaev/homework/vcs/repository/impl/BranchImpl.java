package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Commit;
import org.jetbrains.annotations.NotNull;

public class BranchImpl implements Branch {
  private Commit myCommit;
  private final String myName;


  BranchImpl(@NotNull String name, @NotNull Commit commit) {
    myCommit = commit;
    myName = name;
  }

  @NotNull
  @Override
  public Commit getCommit() {
    return myCommit;
  }

  @NotNull
  @Override
  public String getName() {
    return myName;
  }
}
