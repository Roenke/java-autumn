package com.spbau.bibaev.homework.vcs.repository.api.v2;

import java.util.List;

public interface RepositoryState {
  List<FilePersistentState> getFiles();
}
