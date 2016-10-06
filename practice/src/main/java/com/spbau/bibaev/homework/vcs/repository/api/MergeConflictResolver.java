package com.spbau.bibaev.homework.vcs.repository.api;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface MergeConflictResolver {
  MergeResolvingResult resolve(@NotNull Path file, @NotNull Repository repository);
}
