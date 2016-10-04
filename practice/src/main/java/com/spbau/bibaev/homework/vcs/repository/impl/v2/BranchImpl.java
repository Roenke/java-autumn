package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import org.jetbrains.annotations.NotNull;

public class BranchImpl implements Branch {
  private Commit myCommit;
  private final String myName;

  BranchImpl(@NotNull String name, @NotNull Commit commit) {
    myCommit = commit;
    myName = name;
  }

  @Override
  public Commit getCommit() {
    return myCommit;
  }

  @Override
  public void moveTo(@NotNull Commit commit) {
    myCommit = commit;
  }

  @Override
  public String getName() {
    return myName;
  }
}
