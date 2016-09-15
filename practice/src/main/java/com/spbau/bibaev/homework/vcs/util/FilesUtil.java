package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;

public class FilesUtil {
  public static boolean isContainsDirectory(@NotNull File directory, @NotNull String dirName) {
    return find(directory, File::isDirectory, dirName) != null;
  }

  @Nullable
  public static File findDirectoryByName(@NotNull File directory, @NotNull String name) {
    return find(directory, File::isDirectory, name);
  }

  @Nullable
  public static File findFileByName(@NotNull File directory, @NotNull String filename) {
    return find(directory, File::isFile, filename);
  }

  @Nullable
  private static File find(@NotNull File directory, @NotNull FileFilter filter, @NotNull String filename) {
    File[] files = directory.listFiles(filter);
    if(!directory.isDirectory() || files == null) {
      return null;
    }

    for(File file : files) {
      if(filename.equals(file.getName())) {
        return file;
      }
    }

    return null;
  }
}
