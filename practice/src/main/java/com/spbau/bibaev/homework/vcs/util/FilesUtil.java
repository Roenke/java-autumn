package com.spbau.bibaev.homework.vcs.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class FilesUtil {

  public static boolean isContainsDirectory(@NotNull File directory, @NotNull String dirName) {
    return find(directory, File::isDirectory, dirName) != null;
  }

  public static Collection<String> pathsToStrings(@NotNull Collection<Path> paths) {
    return paths.stream().map(Path::toString).collect(Collectors.toCollection(ArrayList::new));
  }

  public static String evalHashOfFile(@NotNull File file) throws IOException {
    try (InputStream is = Files.newInputStream(file.toPath())) {
      final byte[] digest = DigestUtils.sha1(is);
      return DigestUtils.sha1Hex(digest);
    }
  }

  @Nullable
  private static File find(@NotNull File directory, @NotNull FileFilter filter, @NotNull String filename) {
    File[] files = directory.listFiles(filter);
    if (!directory.isDirectory() || files == null) {
      return null;
    }

    for (File file : files) {
      if (filename.equals(file.getName())) {
        return file;
      }
    }

    return null;
  }
}
