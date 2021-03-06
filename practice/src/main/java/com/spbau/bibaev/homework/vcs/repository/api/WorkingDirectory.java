package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface WorkingDirectory {
  List<Path> getAllFiles();

  Diff getDiff(@NotNull RepositoryState state) throws IOException;

  Path getRootPath();

  void clean();
}
