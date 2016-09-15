package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FilesUtil {
  public static boolean isContainsDirectory(@NotNull File directory, @NotNull String dirName) {
    if(!directory.isDirectory()) {
      return false;
    }

    final File[] directories = directory.listFiles(File::isDirectory);
    if (directories != null) {
      for (File dir : directories) {
        if (dirName.equals(dir.getName())) {
          return true;
        }
      }
    }

    return false;

  }
}
