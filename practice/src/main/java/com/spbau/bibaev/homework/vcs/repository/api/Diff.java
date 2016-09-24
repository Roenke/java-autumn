package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;

public interface Diff {
  @NotNull
  Collection<Path> getNewFiles();

  @NotNull
  Collection<Path> getDeletedFiles();

  @NotNull
  Collection<Path> getModifiedFiles();
}
