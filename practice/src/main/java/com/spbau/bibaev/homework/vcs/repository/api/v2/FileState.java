package com.spbau.bibaev.homework.vcs.repository.api.v2;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.nio.file.Path;

public interface FileState extends Serializable {
  String getRelativePath();

  String getHash();

  String restore(@NotNull Path directory);
}
