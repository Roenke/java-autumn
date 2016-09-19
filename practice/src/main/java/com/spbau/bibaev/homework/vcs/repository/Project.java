package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Project {
  private final Map<String, File> myPath2File;
  private final File myRootDirectory;

  private Project(@NotNull File root, @NotNull Map<String, File> files) {
    myRootDirectory = root;
    myPath2File = files;
  }

  @NotNull
  public List<File> getAllFiles() {
    return myPath2File.values().stream().collect(Collectors.toList());
  }

  @NotNull
  public File getRootDirectory() {
    return myRootDirectory;
  }

  @NotNull
  static Project open(@NotNull File projectRootDirectory, @NotNull File metadataDirectory)
      throws RepositoryOpeningException {
    Path projectPath = projectRootDirectory.toPath();
    Map<String, File> files = new HashMap<>();
    try {
      Files.walkFileTree(projectRootDirectory.toPath(), new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          return dir.equals(metadataDirectory.toPath())
              ? FileVisitResult.SKIP_SUBTREE
              : FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Path relativeFilePath = projectPath.relativize(file);
          files.put(relativeFilePath.toString(), file.toFile());
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
    } catch (IOException e) {
      throw new RepositoryOpeningException("Cannot read project files - " + e.getMessage(), e);
    }
    return new Project(projectRootDirectory, files);
  }
}
