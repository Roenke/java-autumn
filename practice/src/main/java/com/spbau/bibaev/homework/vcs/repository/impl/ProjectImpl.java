package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.Project;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.repository.api.Snapshot;
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

public class ProjectImpl implements Project {
  private final Map<String, File> myPath2File;
  private final File myRootDirectory;

  private ProjectImpl(@NotNull File root, @NotNull Map<String, File> files) {
    myRootDirectory = root;
    myPath2File = files;
  }

  @NotNull
  @Override
  public Snapshot makeSnapshot(@NotNull Path directory) {
    return null;
  }

  @Override
  public @NotNull List<Path> getAllFiles() {
    return myPath2File.values().stream().map(File::toPath).collect(Collectors.toList());
  }

  @Override
  public @NotNull Diff getDiff(@NotNull Revision revision) throws IOException {
    Path projectRoot = myRootDirectory.toPath();
    Collection<Path> projectFiles = getAllFiles();
    Collection<Path> newFiles = new ArrayList<>();
    Collection<Path> modifiedFiles = new ArrayList<>();
    try {
      for (Path path : projectFiles) {
        File file = path.toFile();
        Path relativePath = projectRoot.relativize(file.toPath());
        String hash = FilesUtil.evalHashOfFile(file);
        revision.getHashOfFile(relativePath.toString());
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

    Set<String> deletedFileNames = revision.getFilePaths().stream().map(Path::toString).collect(Collectors.toSet());
    deletedFileNames.removeAll(projectFiles.stream()
        .map(path -> projectRoot.relativize(path).toString())
        .collect(Collectors.toCollection(ArrayList::new)));
    Collection<Path> deleted = deletedFileNames.stream()
        .map(name -> new File(projectRoot.toFile(), name).toPath())
        .collect(Collectors.toCollection(ArrayList::new));

    return new MyDiffImpl(newFiles, deleted, modifiedFiles);
  }

  @Override
  public void rollRevision(@NotNull Revision revision) throws IOException {
    clean();
    revision.getSnapshot().restore(myRootDirectory);
  }

  @NotNull
  public File getRootDirectory() {
    return myRootDirectory;
  }

  @NotNull
  static ProjectImpl open(@NotNull File projectRootDirectory, @NotNull File metadataDirectory)
      throws IOException {
    Path projectPath = projectRootDirectory.toPath();
    Map<String, File> files = new HashMap<>();
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

    return new ProjectImpl(projectRootDirectory, files);
  }

  public void clean() throws IOException {
    final File[] files = myRootDirectory.listFiles((dir, name) -> !name.equals(RepositoryImpl.VCS_DIRECTORY_NAME));
    if (files != null) {
      for (File f : files) {
        FilesUtil.deleteRecursively(f);
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
    public Collection<Path> getNewFiles() {
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
