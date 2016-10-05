package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InitTest {
  @Test
  public void simpleInitInEmptyDirectory() throws IOException {
    Path tempDirectory = Files.createTempDirectory("init-test");
    Repository repository = RepositoryImpl.createRepository(tempDirectory);
    checkRepositoryState(repository);
  }

  @Test
  public void simpleInitInNonEmptyDirectory() throws IOException {
    Path tempDirectory = Files.createTempDirectory("init-test-nonempty");
    String filename = "file.txt";
    Path file = Files.createFile(tempDirectory.resolve(filename));
    Repository repository = RepositoryImpl.createRepository(tempDirectory);
    assertNotNull(repository);
    assertEquals(1, repository.getProject().getAllFiles().size());

    assertEquals(file, repository.getProject().getAllFiles().get(0));
    checkRepositoryState(repository);
  }

  private void checkRepositoryState(Repository repository) {
    assertNotNull("Repository init failed", repository);
    assertNotNull("Current branch is null", repository.getCurrentBranch());
    assertEquals("Init branch should be named master", "master", repository.getCurrentBranch().getName());
    assertNotNull("Must be initial commit not null", repository.getCurrentBranch().getCommit());
    assertEquals("No commit any files", 0, repository.getCurrentBranch().getCommit().getAddedFiles().size());
    assertEquals("No commit any files", 0, repository.getCurrentBranch().getCommit().getModifiedFiles().size());
    assertEquals("No commit any files", 0, repository.getCurrentBranch().getCommit().getDeletedFiles().size());
  }
}