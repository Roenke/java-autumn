package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.FilePersistentState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.WorkingDirectory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class WorkingDirectoryImpl implements WorkingDirectory {
  private final Path myRootPath;

  WorkingDirectoryImpl(Path rootPath) {
    myRootPath = rootPath;
  }

  @Override
  public List<Path> getAllFiles() {
    return FileUtils.listFiles(myRootPath.toFile(),
        FileFilterUtils.trueFileFilter(),
        FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter(RepositoryImpl.REPOSITORY_DIRECTORY_NAME)))
        .stream()
        .map(File::toPath)
        .collect(Collectors.toList());
  }

  @Override
  public Diff getDiff(@NotNull RepositoryState repositoryState) throws IOException {
    List<Path> currentFiles = getAllFiles();
    Map<Path, String> currentFile2Hash = new HashMap<>();
    for (Path file : currentFiles) {
      currentFile2Hash.put(myRootPath.relativize(file), DigestUtils.sha1Hex(Files.newInputStream(file)));
    }

    List<FilePersistentState> repositoryFiles = repositoryState.getFiles();
    Map<Path, String> repositoryFile2Hash = new HashMap<>();
    for (FilePersistentState state : repositoryFiles) {
      Path relativePath = myRootPath.relativize(myRootPath.resolve(state.getRelativePath()));
      repositoryFile2Hash.put(relativePath, state.getHash());
    }

    Map<Path, FileState> relativePath2State = new HashMap<>();
    for (Path file : currentFiles) {
      FileState state;
      String fileHash = currentFile2Hash.get(file);
      if (!repositoryFile2Hash.containsKey(file)) {
        state = FileState.NEW;
      } else {
        String repositoryHash = repositoryFile2Hash.get(file);
        state = repositoryHash.equals(fileHash) ? FileState.NOT_CHANGED : FileState.MODIFIED;
      }

      relativePath2State.put(file, state);
    }

    repositoryFile2Hash.keySet().stream()
        .filter(file -> !repositoryFile2Hash.containsKey(file))
        .forEach(file -> relativePath2State.put(file, FileState.DELETED));

    return new MyDiff(relativePath2State);
  }

  @Override
  public Path getRootDirectory() {
    return myRootPath;
  }

  private static class MyDiff implements Diff {
    private final Collection<Path> myNewFiles;
    private final Collection<Path> myModifiedFiles;
    private final Collection<Path> myDeletedFiles;


    MyDiff(@NotNull Map<Path, FileState> file2State) {
      myNewFiles = file2State.entrySet().stream()
          .filter(e -> e.getValue() == FileState.NEW)
          .map(Map.Entry::getKey).collect(Collectors.toList());
      myModifiedFiles = file2State.entrySet().stream()
          .filter(e -> e.getValue() == FileState.MODIFIED)
          .map(Map.Entry::getKey).collect(Collectors.toList());
      myDeletedFiles = file2State.entrySet().stream()
          .filter(e -> e.getValue() == FileState.DELETED)
          .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public Collection<Path> getNewFiles() {
      return Collections.unmodifiableCollection(myNewFiles);
    }

    @Override
    public Collection<Path> getDeletedFiles() {
      return Collections.unmodifiableCollection(myModifiedFiles);
    }

    @Override
    public Collection<Path> getModifiedFiles() {
      return Collections.unmodifiableCollection(myDeletedFiles);
    }
  }
}
