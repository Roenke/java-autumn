package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Repository {
  private static final String VCS_DIRECTORY_NAME = ".my_vcs";

  /**
   * Open nearest repository (may be in parent folders) or open non-initialized here
   *
   * @param directory Start directory
   * @return Existed repository, if it exists, otherwise empty non-initialized repository
   */
  @NotNull
  public static Repository open(@NotNull File directory) {
    File currentDirectory = directory;
    while (currentDirectory != null && !FilesUtil.isContainsDirectory(currentDirectory, VCS_DIRECTORY_NAME)) {
      currentDirectory = currentDirectory.getParentFile();
    }

    File vcsRoot = currentDirectory == null ? directory : currentDirectory;
    return openHere(vcsRoot);
  }

  /** Open/create repository here
   * @param directory directory for repository creating
   * @return repository
   */
  public static Repository openHere(@NotNull File directory) {
    return new Repository(directory);
  }

  private Repository(@NotNull File directory) {

  }

  /**
   * Check that metadata created
   *
   * @return true, if metadata already created, false otherwise
   */
  public boolean isInitialized() {
    return false;
  }

  /**
   * Create metadata files for current directory
   *
   * @return true, if metadata successfully created, false if it already exists
   */
  public boolean initialize() {
    return false;
  }

  private class Storage {
  }
}
