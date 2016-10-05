package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import org.apache.commons.io.FileUtils;
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
      write(Files.createFile(testDirectory.resolve(FILE)), "text");
      write(Files.createFile(testDirectory.resolve(MAKEFILE)), "makefile content");

      final Path srcDirectory = Files.createDirectory(testDirectory.resolve(DIRECTORY));
      write(Files.createFile(testDirectory.resolve(NESTED_FILE1)), "void main (int argc, char** argv) {}");
      write(Files.createFile(testDirectory.resolve(NESTED_FILE2)), "int id(int n) {return n;}");
      write(Files.createFile(testDirectory.resolve(NESTED_FILE3)), "#include <stream>");

      Files.createDirectory(srcDirectory.resolve(NESTED_DIRECTORY));
      write(Files.createFile(testDirectory.resolve(NESTED_NESTED_FILE)), "some impl");
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

  protected void checkStateNotChanged(Repository before, Repository after) {
    assertEquals(before.getCurrentBranch().getName(), after.getCurrentBranch().getName());
    assertEquals(before.getCurrentBranch().getCommit().getMeta().getDate(),
        after.getCurrentBranch().getCommit().getMeta().getDate());
    assertEquals(before.getWorkingDirectory().getAllFiles().size(), after.getWorkingDirectory().getAllFiles().size());
    assertEquals(before.getUserName(), after.getUserName());

    assertTrue(before.getBranches().stream()
        .allMatch(beforeBranch -> after.getBranches().stream()
            .anyMatch(x -> x.getName().equals(beforeBranch.getName()))));

    assertTrue(after.getBranches().stream()
        .allMatch(afterBranch -> before.getBranches().stream()
            .anyMatch(x -> x.getName().equals(afterBranch.getName()))));
  }

  private void write(Path file, String data) throws IOException {
    FileUtils.write(file.toFile(), data, "UTF-8", true);
  }

  protected Path getDirectory() {
    return myRule.getRoot().toPath();
  }
}

