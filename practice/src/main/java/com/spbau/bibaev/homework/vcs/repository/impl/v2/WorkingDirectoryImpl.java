package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.WorkingDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class WorkingDirectoryImpl implements WorkingDirectory {
  private final Path myRootPath;

  WorkingDirectoryImpl(Path rootPath) {
    myRootPath = rootPath;
  }

  @Override
  public List<Path> getAllFiles() {
    return null;
  }

  @Override
  public Diff getDiff(@NotNull RepositoryState state) throws IOException {
    return null;
  }

  @Override
  public Path getRootDirectory() {
    return myRootPath;
  }
}
