package com.spbau.bibaev.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by roenk on 05.10.2016.
 */
public class Main {
  public static void main(String[] args) throws IOException {
    final Path tempFile = Files.createTempFile(Paths.get(System.getProperty("user.dir")), "tmp", "tmp");
    tempFile.toFile().delete();
  }
}
