package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Project {
  @NotNull
  List<Path> getAllFiles();

  @NotNull
  Diff getDiff(@NotNull Revision revision) throws IOException;

  @NotNull
  Path getRootDirectory();

  void clean() throws IOException;

  void rollRevision(@NotNull Revision revision) throws IOException;
}
