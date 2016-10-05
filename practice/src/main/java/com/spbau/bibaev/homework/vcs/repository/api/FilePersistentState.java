package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

public interface FilePersistentState {
  String getRelativePath();

  String getHash() throws IOException;

  void restore(@NotNull Path directory) throws IOException;
}
