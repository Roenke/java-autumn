package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Deprecated
public class RepositoryFacade {
  private static class Holder {
    static final RepositoryFacade INSTANCE = new RepositoryFacade();
  }

  public static RepositoryFacade getInstance() {
    return Holder.INSTANCE;
  }

  @Nullable
  public Repository openRepository(@NotNull Path directory) throws IOException {
    File currentDirectory = directory.toFile();
    while (currentDirectory != null && !FilesUtil.isContainsDirectory(currentDirectory, RepositoryImpl.VCS_DIRECTORY_NAME)) {
      currentDirectory = currentDirectory.getParentFile();
    }

    if (currentDirectory == null) {
      return null;
    }

    return RepositoryImpl.openHere(directory.toFile());
  }

  @Nullable
  public Repository initRepository(@NotNull Path directory) throws IOException {
    if (openRepository(directory) != null) {
      return null;
    }

    return RepositoryImpl.createNewRepository(directory.toFile());
  }
}
