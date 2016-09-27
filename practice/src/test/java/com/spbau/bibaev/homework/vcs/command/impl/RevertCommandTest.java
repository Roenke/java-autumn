package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class RevertCommandTest extends RepositoryTestCase {
  @Test
  public void revertSimpleChange() throws IOException {
    final Repository repository = openRepository();
    assertNotNull(repository);
    repository.commitChanges("commit");

    List<Path> files = repository.getProject().getAllFiles();
    assertNotEquals(0, files.size());
    final Path file = files.get(0);
    final String hashBeforeChange = FilesUtil.evalHashOfFile(file.toFile());
    Files.write(file, "abs".getBytes());
    final String hashAfterChange = FilesUtil.evalHashOfFile(file.toFile());
    assertNotEquals(hashBeforeChange, hashAfterChange);

    new RevertCommand(myRule.getRoot().toPath()).perform(Collections.emptyList());
    final Repository updated = openRepository();
    final Path updatedPath = updated.getProject().getAllFiles().stream()
        .filter(path -> path.toString().equals(file.toString())).findFirst().orElse(null);

    String hashAfterRevert = FilesUtil.evalHashOfFile(updatedPath.toFile());
    assertNotEquals(hashAfterChange, hashAfterRevert);
    assertEquals(hashBeforeChange, hashAfterRevert);
  }
}
