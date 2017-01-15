package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

public interface Commit extends Serializable {
  @NotNull
  List<Commit> getParents();

  @NotNull
  Path getSnapshotFile();

  @Nullable
  Commit getMainParent();

  @NotNull
  CommitMeta getMeta();

  @NotNull
  List<FilePersistentState> getAddedFiles();

  @NotNull
  List<FilePersistentState> getModifiedFiles();

  @NotNull
  List<String> getDeletedFiles();

  @NotNull
  RepositoryState getRepositoryState();
}
