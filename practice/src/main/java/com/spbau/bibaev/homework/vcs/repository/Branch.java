package com.spbau.bibaev.homework.vcs.repository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Branch {
  public Branch(@NotNull String name) {

  }

  public Branch(@NotNull String name, @NotNull Branch branchParent) {
  }

  @NotNull
  public List<Revision> getRevisions() {
    return new ArrayList<>();
  }

  @NotNull
  public String getName() {
    return "";
  }
}
