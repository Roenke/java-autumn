package com.spbau.bibaev.homework.vcs;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Repository {
  private static final String VCS_DIRECTORY_SUFFIX = File.separator + ".my_vcs" + File.separator;

  private Repository(@NotNull File directory) {
    String repPath = directory.getAbsolutePath() + VCS_DIRECTORY_SUFFIX;
  }

  /** Check that metadata created
   * @return true, if metadate already created, false otherwise
   */
  public boolean isInitialized() {
    return false;
  }


  /** Create metadata files for current directory
   * @return true, if metadata successfully created, false if it already exists
   */
  public boolean initialize() {
    return false;
  }

  @NotNull
  public static Repository open(@NotNull File projectDirectory) {
    return new Repository(projectDirectory);
  }

  private class Storage {
  }
}
