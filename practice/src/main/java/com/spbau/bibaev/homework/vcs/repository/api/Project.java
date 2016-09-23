package com.spbau.bibaev.homework.vcs.repository.api;

import com.spbau.bibaev.homework.vcs.util.Diff;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Project {
  @NotNull
  Snapshot makeSnapshot(@NotNull Path directory);

  @NotNull
  List<Path> getAllFiles();

  @NotNull
  Diff getDiff(@NotNull Revision revision) throws IOException;

  @NotNull
  Path getRootDirectory();

  void clean() throws IOException;

  void rollRevision(@NotNull Revision revision) throws IOException;
}
