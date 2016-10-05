package com.spbau.bibaev.homework.vcs.serialization;

import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class RepositorySerialization {
  @Rule
  public TemporaryFolder myRule = new TemporaryFolder() {
    @Override
    protected void before() throws Throwable {
      super.before();
      final Path testDirectory = myRule.getRoot().toPath();
      RepositoryImpl.createRepository(testDirectory);

      final Path srcDirectory = Files.createDirectory(testDirectory.resolve("src"));
      Files.createFile(srcDirectory.resolve("code.cpp"));
      Files.createFile(srcDirectory.resolve("main.cpp"));
      Files.createFile(srcDirectory.resolve("lib.h"));

      Path nested = Files.createDirectory(srcDirectory.resolve("impl"));
      Files.createFile(nested.resolve("impl.txt"));
    }
  };

  @Test
  public void check() throws IOException, ClassNotFoundException {
    RepositoryImpl repository = RepositoryImpl.openRepository(myRule.getRoot().toPath());
    assertEquals(myRule.getRoot().toPath(), repository.getMetaDirectory().getParent());
    assertEquals(RepositoryImpl.REPOSITORY_DIRECTORY_NAME, repository.getMetaDirectory().getFileName().toString());
    assertEquals(myRule.getRoot().toPath(), repository.getWorkingDirectory().getRootPath());
    assertTrue(repository.getMetaDirectory().toFile().exists());
    assertEquals(RepositoryImpl.DEFAULT_BRANCH_NAME, repository.getCurrentBranch().getName());
  }
}
