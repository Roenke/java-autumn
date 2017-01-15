package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;

public interface Diff {
  Collection<Path> getNewFiles();

  Collection<Path> getDeletedFiles();

  Collection<Path> getModifiedFiles();

  FileState getFileState(@NotNull String relativePath);
}
