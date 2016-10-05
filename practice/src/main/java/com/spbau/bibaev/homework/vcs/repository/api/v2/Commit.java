package com.spbau.bibaev.homework.vcs.repository.api.v2;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface Commit extends Serializable {
  List<Commit> getParents();

  @Nullable
  Commit getMainParent();

  CommitMeta getMeta();

  List<FilePersistentState> getAddedFiles();

  List<FilePersistentState> getModifiedFiles();

  List<String> getDeletedFiles();

  RepositoryState getRepositoryState();
}
