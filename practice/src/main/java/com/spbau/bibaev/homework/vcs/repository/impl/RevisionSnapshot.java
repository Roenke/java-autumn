package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.Snapshot;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class RevisionSnapshot implements Snapshot {
  private final Path myPathToSnapshot;
  private final Map<String, FileDescriptor> myPositionMapping;

  RevisionSnapshot(@NotNull Path file, @NotNull Map<String, FileDescriptor> positionsMapping) {
    myPathToSnapshot = file;
    myPositionMapping = new HashMap<>(positionsMapping);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void restore(@NotNull Path directory) throws IOException {
    for (String pathSuffix : myPositionMapping.keySet()) {
      FileDescriptor descriptor = myPositionMapping.get(pathSuffix);

      Path path = directory.resolve(pathSuffix);
      Files.createDirectories(path.getParent());
      File outputFile = Files.createFile(path).toFile();

      FileInputStream stream = new FileInputStream(myPathToSnapshot.toFile());
      stream.skip(descriptor.offset);
      writeToFile(stream, outputFile, descriptor.length);
      stream.close();
    }
  }

  private static void writeToFile(@NotNull InputStream in, @NotNull File file, long len) throws IOException {
    try (OutputStream out = new FileOutputStream(file)) {
      byte[] buffer = new byte[4096];
      long remain = len;
      while (remain > 0) {
        int readBytes = in.read(buffer, 0, (int) Math.min(buffer.length, remain));
        out.write(buffer, 0, readBytes);
        remain -= readBytes;
      }
    }
  }
}
