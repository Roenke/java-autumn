package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public interface Repository {
  @NotNull
  Project getProject();

  @NotNull
  List<Branch> getBranches();

  @NotNull
  String getUserName();

  void setUserName(@NotNull String name) throws IOException;

  @NotNull
  Branch getCurrentBranch();

  @NotNull
  Branch createNewBranch(@NotNull String name) throws IOException;

  @Nullable
  Branch getBranchByName(@NotNull String name);

  @NotNull
  Revision commitChanges(@NotNull String message) throws IOException;

  @NotNull
  Revision checkout(@NotNull Branch branch) throws IOException;

  @NotNull
  Revision checkout(@NotNull Revision revision) throws IOException;
}
