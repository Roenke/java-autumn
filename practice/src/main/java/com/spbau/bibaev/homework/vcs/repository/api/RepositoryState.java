package com.spbau.bibaev.homework.vcs.repository.api;

import java.util.List;

public interface RepositoryState {
  List<FilePersistentState> getFiles();
}
