package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.FilePersistentState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.WorkingDirectory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
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
      final MessageDigest sha1Digest = DigestUtils.getSha1Digest();
      try (InputStream is = new DigestInputStream(Files.newInputStream(file), sha1Digest)) {
        DigestUtils.sha1Hex(is);
        currentFile2Hash.put(myRootPath.relativize(file), DigestUtils.sha1Hex(sha1Digest.digest()));
      }
    }

    List<FilePersistentState> repositoryFiles = repositoryState.getFiles();
    Map<Path, String> repositoryFile2Hash = new HashMap<>();
    for (FilePersistentState state : repositoryFiles) {
      Path relativePath = myRootPath.relativize(myRootPath.resolve(state.getRelativePath()));
      repositoryFile2Hash.put(relativePath, state.getHash());
    }

    Map<Path, FileState> relativePath2State = new HashMap<>();
    for (Path file : currentFiles) {
      file = myRootPath.relativize(file);
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
  public Path getRootPath() {
    return myRootPath;
  }

  @Override
  public void clean() {
    getAllFiles().forEach(x -> FileUtils.deleteQuietly(x.toFile()));
    final File[] files = myRootPath.toFile()
        .listFiles((dir, name) -> !RepositoryImpl.REPOSITORY_DIRECTORY_NAME.equals(name));
    if (files != null) {
      for (File file : files) {
        FileUtils.deleteQuietly(file);
      }
    }
  }

  private class MyDiff implements Diff {
    private final Map<Path, FileState> myPath2State;

    MyDiff(@NotNull Map<Path, FileState> file2State) {
      myPath2State = new HashMap<>(file2State);
    }

    @Override
    public Collection<Path> getNewFiles() {
      return Collections.unmodifiableCollection(myPath2State.entrySet().stream()
          .filter(e -> e.getValue() == FileState.NEW)
          .map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    @Override
    public Collection<Path> getDeletedFiles() {
      return Collections.unmodifiableCollection(myPath2State.entrySet().stream()
          .filter(e -> e.getValue() == FileState.DELETED)
          .map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    @Override
    public Collection<Path> getModifiedFiles() {
      return Collections.unmodifiableCollection(myPath2State.entrySet().stream()
          .filter(e -> e.getValue() == FileState.MODIFIED)
          .map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    @Override
    public FileState getFileState(@NotNull String relativePath) {
      Path path = myPath2State.keySet().stream().filter(p -> p.toString().equals(relativePath))
          .findFirst().orElse(null);
      return path == null ? FileState.UNKNOWN : myPath2State.get(path);
    }
  }
}
