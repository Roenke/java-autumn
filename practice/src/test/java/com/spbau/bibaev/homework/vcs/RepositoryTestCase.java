package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryFacade;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RepositoryTestCase {
  @Rule
  public TemporaryFolder myRule = new TemporaryFolder() {
    @Override
    protected void before() throws Throwable {
      super.before();
      final Path testDirectory = myRule.getRoot().toPath();
      RepositoryFacade.getInstance().initRepository(testDirectory);
      Files.createFile(testDirectory.resolve("file.txt"));
      Files.createFile(testDirectory.resolve("Makefile.txt"));

      final Path srcDirectory = Files.createDirectory(testDirectory.resolve("src"));
      Files.createFile(srcDirectory.resolve("code.cpp"));
      Files.createFile(srcDirectory.resolve("main.cpp"));
      Files.createFile(srcDirectory.resolve("lib.h"));

      Path nested = Files.createDirectory(srcDirectory.resolve("impl"));
      Files.createFile(nested.resolve("impl.txt"));
    }
  };

  protected Repository openRepository() throws IOException {
    return RepositoryFacade.getInstance().openRepository(myRule.getRoot().toPath());
  }

  protected void addFile(String name, String content) throws IOException {
    final File file = myRule.newFile(name);
    Files.write(file.toPath(), content.getBytes());
  }

  protected void checkStateNotChanged(Repository before, Repository after) {
    assertEquals(before.getCurrentBranch().getName(), after.getCurrentBranch().getName());
    assertEquals(before.getCurrentBranch().getLastRevision().getDate(),
        after.getCurrentBranch().getLastRevision().getDate());
    assertEquals(before.getProject().getAllFiles().size(), after.getProject().getAllFiles().size());
    assertEquals(before.getCurrentBranch().getRevisions().size(), after.getCurrentBranch().getRevisions().size());
    assertEquals(before.getUserName(), after.getUserName());

    assertTrue(before.getBranches().stream()
        .allMatch(beforeBranch -> after.getBranches().stream()
            .anyMatch(x -> x.getName().equals(beforeBranch.getName()))));

    assertTrue(after.getBranches().stream()
        .allMatch(afterBranch -> before.getBranches().stream()
            .anyMatch(x -> x.getName().equals(afterBranch.getName()))));
  }
}

