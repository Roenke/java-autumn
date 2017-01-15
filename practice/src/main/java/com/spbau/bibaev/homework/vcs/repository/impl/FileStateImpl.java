package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.FilePersistentState;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStateImpl implements FilePersistentState, Serializable {
  private final String myRelativePath;
  private final int mySnapshotOffset;
  private final int mySnapshotLength;
  private final String myHash;
  private Commit myCommit;

  FileStateImpl(@NotNull String relativePath, @Nullable Commit commit, @NotNull String hash, int off, int len) {
    myRelativePath = relativePath;
    myCommit = commit;
    mySnapshotOffset = off;
    mySnapshotLength = len;
    myHash = hash;
  }

  @Override
  public String getRelativePath() {
    return myRelativePath;
  }

  public String getHash() {
    return myHash;
  }

  @Override
  public void restore(@NotNull Path directory) throws IOException {
    final Path snapshotFile = myCommit.getSnapshotFile();
    try (final InputStream is = Files.newInputStream(snapshotFile)) {
      final Path fileLocation = directory.resolve(myRelativePath);
      Files.createDirectories(fileLocation.getParent());
      Files.createFile(fileLocation);
      IOUtils.skip(is, mySnapshotOffset);
      writeToFile(is, fileLocation, mySnapshotLength);
    }
  }

  private static void writeToFile(@NotNull InputStream in, @NotNull Path path, int len) throws IOException {
    try (OutputStream out = Files.newOutputStream(path)) {
      copy(in, out, len);
    }
  }

  private static void copy(@NotNull InputStream in, @NotNull OutputStream out, int len) throws IOException {
    byte[] buffer = new byte[4096];
    int remain = len;
    while (remain > 0) {
      int readBytes = in.read(buffer, 0, Math.min(buffer.length, remain));
      out.write(buffer, 0, readBytes);
      remain -= readBytes;
    }
  }

  public void setCommit(CommitImpl commit) {
    myCommit = commit;
  }
}
