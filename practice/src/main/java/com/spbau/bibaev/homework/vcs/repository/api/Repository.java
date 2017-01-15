package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Repository {
  @NotNull
  WorkingDirectory getWorkingDirectory();

  @NotNull
  Path getMetaDirectory();

  @NotNull
  List<Branch> getBranches();

  @NotNull
  String getUserName();

  void setUserName(@NotNull String name) throws IOException;

  @NotNull
  Branch getCurrentBranch();

  @NotNull
  Branch createNewBranch(@NotNull String name, @NotNull Commit commit) throws IOException;

  @Nullable
  Branch getBranchByName(@NotNull String name);

  @Nullable
  Commit getCommit(@NotNull String commitId);

  @NotNull
  RepositoryIndex getIndex();

  boolean addFileToIndex(@NotNull Path pathToFile);

  boolean removeFileFromIndex(@NotNull Path pathToFile);

  @NotNull
  Commit commitChanges(@NotNull String message) throws IOException;

  @Nullable
  Commit merge(@NotNull Commit commit, @Nullable String message, @NotNull MergeConflictResolver resolver);

  @NotNull
  Commit checkout(@NotNull Branch branch) throws IOException;

  @NotNull
  Commit checkout(@NotNull Commit revision) throws IOException;

  void save() throws IOException;
}
