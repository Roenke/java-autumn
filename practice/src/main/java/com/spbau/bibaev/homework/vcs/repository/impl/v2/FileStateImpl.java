package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.FileState;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class FileStateImpl implements FileState {
  private static final OutputStream NULL = new OutputStream() {
    @Override
    public void write(int b) throws IOException {
    }
  };

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
  public void restore(@NotNull Path directory) throws IOException {
    InputStream is = Files.newInputStream(mySnapshotFile);
    Path fileLocation = directory.resolve(myRelativePath);
    Files.createDirectories(fileLocation.getParent());
    Files.createFile(fileLocation);
    is.skip(mySnapshotOffset);
    writeToFile(is, fileLocation, mySnapshotLength);
  }

  private String evalHash() throws IOException {
    InputStream is = Files.newInputStream(mySnapshotFile);
    MessageDigest digest = DigestUtils.getSha1Digest();
    DigestInputStream dis = new DigestInputStream(is, digest);
    is.skip(mySnapshotOffset);
    copy(dis, NULL, mySnapshotLength);
    hash = DigestUtils.sha1Hex(digest.digest());

    return hash;
  }

  private static void writeToFile(@NotNull InputStream in, @NotNull Path path, long len) throws IOException {
    try (OutputStream out = Files.newOutputStream(path)) {
      copy(in, out, len);
    }
  }

  private static void copy(@NotNull InputStream in, @NotNull OutputStream out, long len) throws IOException {
    byte[] buffer = new byte[4096];
    long remain = len;
    while (remain > 0) {
      int readBytes = in.read(buffer, 0, (int) Math.min(buffer.length, remain));
      out.write(buffer, 0, readBytes);
      remain -= readBytes;
    }
  }
}
