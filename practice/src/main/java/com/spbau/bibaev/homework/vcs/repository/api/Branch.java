package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

public interface Branch {
  @NotNull
  List<Revision> getRevisions();

  @NotNull
  Revision getLastRevision();

  @NotNull
  String getName();

  @NotNull
  Revision commitChanges(@NotNull Revision revision);
}
