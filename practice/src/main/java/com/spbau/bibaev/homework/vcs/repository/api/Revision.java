package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

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
  String getHashOfFile(@NotNull String path);

  @NotNull
  List<Path> getFilePaths();

  @NotNull
  Snapshot getSnapshot();
}
