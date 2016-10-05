package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

@SuppressWarnings("WeakerAccess")
public class RepositoryTestCase {
  protected static final String MAKEFILE = "Makefile";
  protected static final String FILE = "file.txt";
  protected static final String DIRECTORY = "src";
  protected static final String NESTED_DIRECTORY = "impl";

  protected static final String NESTED_FILE1 = DIRECTORY + File.separator + "code.cpp";
  protected static final String NESTED_FILE2 = DIRECTORY + File.separator + "main.cpp";
  protected static final String NESTED_FILE3 = DIRECTORY + File.separator + "lib.h";
  protected static final String NESTED_NESTED_FILE = DIRECTORY + File.separator + NESTED_DIRECTORY + File.separator + "impl.txt";
  @Rule
  public TemporaryFolder myRule = new TemporaryFolder() {
    @Override
    protected void before() throws Throwable {
      super.before();
      final Path testDirectory = myRule.getRoot().toPath();
      RepositoryImpl.createRepository(testDirectory);
      Files.createFile(testDirectory.resolve(FILE));
      Files.createFile(testDirectory.resolve(MAKEFILE));

      final Path srcDirectory = Files.createDirectory(testDirectory.resolve(DIRECTORY));
      Files.createFile(testDirectory.resolve(NESTED_FILE1));
      Files.createFile(testDirectory.resolve(NESTED_FILE2));
      Files.createFile(testDirectory.resolve(NESTED_FILE3));

      Files.createDirectory(srcDirectory.resolve(NESTED_DIRECTORY));
      Files.createFile(testDirectory.resolve(NESTED_NESTED_FILE));
    }
  };

  protected Repository openRepository() throws IOException {
    return RepositoryImpl.openRepository(getDirectory());
  }

  @NotNull
  protected Command createCommand(@NotNull String name) {
    @Nullable final Command command = CommandFactory.createCommand(getDirectory(), name);
    assertNotNull(command);
    return command;
  }

  protected void addFile(String name, String content) throws IOException {
    final File file = myRule.newFile(name);
    Files.write(file.toPath(), content.getBytes());
  }

  protected void checkStateNotChanged(Repository before, Repository after) {
    assertEquals(before.getCurrentBranch().getName(), after.getCurrentBranch().getName());
    assertEquals(before.getCurrentBranch().getCommit().getMeta().getDate(),
        after.getCurrentBranch().getCommit().getMeta().getDate());
    assertEquals(before.getProject().getAllFiles().size(), after.getProject().getAllFiles().size());
    assertEquals(before.getUserName(), after.getUserName());

    assertTrue(before.getBranches().stream()
        .allMatch(beforeBranch -> after.getBranches().stream()
            .anyMatch(x -> x.getName().equals(beforeBranch.getName()))));

    assertTrue(after.getBranches().stream()
        .allMatch(afterBranch -> before.getBranches().stream()
            .anyMatch(x -> x.getName().equals(afterBranch.getName()))));
  }

  protected Path getDirectory() {
    return myRule.getRoot().toPath();
  }
}

