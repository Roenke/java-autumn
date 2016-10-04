package com.spbau.bibaev.homework.vcs.repository.api;

import java.nio.file.Path;
import java.util.Collection;

public interface Diff {
  Collection<Path> getNewFiles();

  Collection<Path> getDeletedFiles();

  Collection<Path> getModifiedFiles();
}
