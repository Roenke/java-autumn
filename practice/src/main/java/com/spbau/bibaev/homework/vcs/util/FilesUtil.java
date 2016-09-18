package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

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

  public static void recursiveCopyDirectory(@NotNull Path from, @NotNull Path to) throws IOException {
    Files.walkFileTree(from, new FileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Files.createDirectories(to.resolve(from.relativize(dir)));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, to.resolve(from.relativize(file)));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }
    });
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
