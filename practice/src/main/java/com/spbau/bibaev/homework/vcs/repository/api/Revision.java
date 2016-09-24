package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public interface Revision {
  @NotNull
  String getHash();

  @NotNull
  Date getDate();

  @NotNull
  String getMessage();

  @NotNull
  String getAuthorName();

  @NotNull
  FileState getFileState(@NotNull Path relativePath) throws IOException;

  String getHashOfFile(@NotNull String relativePath);

  @NotNull
  List<Path> getFilePaths();

  @NotNull
  Snapshot getSnapshot();
}
