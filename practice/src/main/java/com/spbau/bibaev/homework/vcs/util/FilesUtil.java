package com.spbau.bibaev.homework.vcs.util;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

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

  public static void copy(@NotNull InputStream in, @NotNull OutputStream out) throws IOException {
    byte[] buffer = new byte[4096];
    int n = in.read(buffer);
    while (n > 0) {
      out.write(buffer, 0, n);
      n = in.read(buffer);
    }
    in.close();
  }

  public static void deleteRecursively(@NotNull File file) throws IOException {
    if (file.isDirectory()) {
      FileUtils.forceDelete(file);
    } else {
      FileUtils.deleteQuietly(file);
    }

  }

  private static void createDirectoryRecursively(@NotNull File directory) throws IOException {
    if (!directory.exists()) {
      createDirectoryRecursively(directory.getParentFile());
      Files.createDirectory(directory.toPath());
    }
  }

  public static void createFile(@NotNull File file) throws IOException {
    if (file.exists()) {
      return;
    }

    createDirectoryRecursively(file.getParentFile());
    Files.createFile(file.toPath());
  }

  public static Collection<String> pathsToStrings(@NotNull Collection<Path> paths) {
    return paths.stream().map(Path::toString).collect(Collectors.toCollection(ArrayList::new));
  }

  private static Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

  public static String evalHashOfFile(@NotNull File file) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      DigestInputStream stream = new DigestInputStream(Files.newInputStream(file.toPath()), digest);
      byte[] buffer = new byte[4096];
      int count = stream.read(buffer);
      while (count > 0) {
        count = stream.read(buffer);
      }

      return BASE64_ENCODER.encodeToString(digest.digest());
    } catch (NoSuchAlgorithmException ignored) {
      throw new UnsupportedEncodingException();
    }
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
