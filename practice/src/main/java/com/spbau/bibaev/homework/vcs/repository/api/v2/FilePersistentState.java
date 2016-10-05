package com.spbau.bibaev.homework.vcs.repository.api.v2;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

public interface FilePersistentState {
  String getRelativePath();

  String getMyHash() throws IOException;

  void restore(@NotNull Path directory) throws IOException;
}
