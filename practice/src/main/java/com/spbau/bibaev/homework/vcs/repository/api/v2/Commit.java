package com.spbau.bibaev.homework.vcs.repository.api.v2;

import java.io.Serializable;
import java.util.List;

public interface Commit extends Serializable {
  List<Commit> getParents();

  CommitMeta getMeta();

  List<FilePersistentState> getAddedFiles();

  List<FilePersistentState> getModifiedFiles();

  List<FilePersistentState> getDeletedFiles();

  RepositoryState getRepositoryState();
}
