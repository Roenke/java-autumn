package com.spbau.bibaev.homework.vcs.repository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Branch {

  @NotNull
  public List<Revision> getRevisions() {
    return new ArrayList<>();
  }

  @NotNull
  public String getName() {
    return "";
  }
}
