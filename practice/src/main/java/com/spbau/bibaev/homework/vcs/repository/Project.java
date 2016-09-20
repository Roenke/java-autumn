package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.Diff;
import com.spbau.bibaev.homework.vcs.util.FileState;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
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
  public Diff diffWithRevision(@NotNull Revision revision) {
    Path projectRoot = myRootDirectory.toPath();
    Collection<File> projectFiles = getAllFiles();
    Collection<Path> newFiles = new ArrayList<>();
    Collection<Path> modifiedFiles = new ArrayList<>();
    try {
      for (File file : projectFiles) {
        Path relativePath = projectRoot.relativize(file.toPath());
        String hash = FilesUtil.evalHashOfFile(file);
        FileState state = revision.getFileState(relativePath.toString(), hash);
        switch (state) {
          case MODIFIED:
            modifiedFiles.add(relativePath);
            break;
          case NEW:
            newFiles.add(relativePath);
            break;
        }
      }
    } catch (IOException e) {
      ConsoleColoredPrinter.println("Error occurred: " + e.getMessage());
    }

    Set<String> deletedFileNames = revision.getAllFiles().stream().collect(Collectors.toSet());
    deletedFileNames.removeAll(projectFiles.stream()
        .map(file -> projectRoot.relativize(file.toPath()).toString())
        .collect(Collectors.toCollection(ArrayList::new)));
    Collection<Path> deleted = deletedFileNames.stream()
        .map(name -> new File(projectRoot.toFile(), name).toPath())
        .collect(Collectors.toCollection(ArrayList::new));

    return new MyDiffImpl(newFiles, deleted, modifiedFiles);
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

  public void clean() throws RepositoryIOException {
    for(String path : myPath2File.keySet()) {
      File file = myPath2File.get(path);
      if(!file.delete()) {
        throw new RepositoryIOException("Cannot delete file in current project: " + path);
      }
    }

    myPath2File.clear();
  }

  private static class MyDiffImpl implements Diff {
    private final Collection<Path> myNew;
    private final Collection<Path> myDeleted;
    private final Collection<Path> myModified;

    MyDiffImpl(@NotNull Collection<Path> newFiles, @NotNull Collection<Path> deletedFiles,
               @NotNull Collection<Path> modifiedFiles) {
      myNew = newFiles;
      myDeleted = deletedFiles;
      myModified = modifiedFiles;
    }


    @Override
    @NotNull
    public  Collection<Path> getNewFiles() {
      return Collections.unmodifiableCollection(myNew);
    }

    @Override
    @NotNull
    public Collection<Path> getDeletedFiles() {
      return Collections.unmodifiableCollection(myDeleted);
    }

    @Override
    @NotNull
    public Collection<Path> getModifiedFiles() {
      return Collections.unmodifiableCollection(myModified);
    }
  }
}
