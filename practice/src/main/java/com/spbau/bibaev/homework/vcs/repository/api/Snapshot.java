package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public interface Snapshot {
  void restore(@NotNull Path directory) throws IOException;
}
