package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.FileState;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class FileStateImpl implements FileState {
  private final String myRelativePath;
  private final Path mySnapshotFile;
  private final int mySnapshotOffset;
  private final int mySnapshotLength;
  private transient String hash = "";

  public FileStateImpl(String relativePath, Path snapshotFile, int off, int len) {
    myRelativePath = relativePath;
    mySnapshotFile = snapshotFile;
    mySnapshotOffset = off;
    mySnapshotLength = len;
  }

  @Override
  public String getRelativePath() {
    return myRelativePath;
  }

  @Override
  public String getHash() {
    if(hash.isEmpty()) {
      return evalHash();
    }

    return hash;
  }

  @Override
  public String restore(@NotNull Path directory) {
    // TODO
    return null;
  }

  private String evalHash() {
    return hash;
  }
}
