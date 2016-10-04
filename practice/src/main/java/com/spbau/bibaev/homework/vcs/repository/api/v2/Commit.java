package com.spbau.bibaev.homework.vcs.repository.api.v2;

import java.io.Serializable;
import java.util.List;

public interface Commit extends Serializable {
  List<Commit> getParents();

  CommitMeta getMeta();

  List<FileState> getAddedFiles();

  List<FileState> getModifiedFiles();

  List<FileState> getDeletedFiles();

  RepositoryState getRepositoryState();
}
