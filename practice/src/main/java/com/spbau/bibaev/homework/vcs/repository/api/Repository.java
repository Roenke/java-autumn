package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Repository {
  WorkingDirectory getWorkingDirectory();

  List<Branch> getBranches();

  String getUserName();

  void setUserName(@NotNull String name) throws IOException;

  Branch getCurrentBranch();

  Branch createNewBranch(@NotNull String name, @NotNull Commit commit) throws IOException;

  @Nullable
  Branch getBranchByName(@NotNull String name);

  Commit getCommit(@NotNull String commitId);

  RepositoryIndex getIndex();

  boolean addFileToIndex(@NotNull Path pathToFile);

  boolean removeFileFromIndex(@NotNull Path pathToFile);

  Commit commitChanges(@NotNull String message) throws IOException;

  Commit merge(@NotNull Commit commit, @Nullable String message);

  Commit checkout(@NotNull Branch branch) throws IOException;

  Commit checkout(@NotNull Commit revision) throws IOException;

  void save() throws IOException;
}
