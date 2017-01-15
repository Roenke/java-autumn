package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ResetCommandTest extends RepositoryTestCase{
  @Test
  public void resetUnknownFile() {
    assertEquals(CommandResult.FAILED, createCommand("reset").perform(Collections.singletonList(MAKEFILE)));
  }

  @Test
  public void simpleReset() throws IOException {
    assertEquals(CommandResult.SUCCESSFUL, createCommand("add").perform(Collections.singletonList(MAKEFILE)));
    assertEquals(CommandResult.SUCCESSFUL, createCommand("commit").perform(Collections.singletonList("message")));

    final Path makefilePath = getDirectory().resolve(MAKEFILE);
    String contentBefore, contentAfter;
    try(InputStream is = Files.newInputStream(makefilePath)) {
      contentBefore = IOUtils.readLines(is, "UTF-8").get(0);
    }
    FileUtils.write(makefilePath.toFile(), "addition", "UTF-8", true);
    try(InputStream is = Files.newInputStream(makefilePath)){
      contentAfter = IOUtils.readLines(is, "UTF-8").get(0);
    }
    assertNotEquals(contentAfter, contentBefore);

    assertEquals(CommandResult.SUCCESSFUL, createCommand("reset").perform(Collections.singletonList(MAKEFILE)));
    try(InputStream is = Files.newInputStream(makefilePath)) {
      String afterReset = IOUtils.readLines(is, "UTF-8").get(0);
      assertEquals(afterReset, contentBefore);
    }
  }

  @Test
  public void resetRemoved() {
    assertEquals(CommandResult.SUCCESSFUL, createCommand("add").perform(Collections.singletonList(MAKEFILE)));
    assertEquals(CommandResult.SUCCESSFUL, createCommand("commit").perform(Collections.singletonList("message")));

    final Path makefilePath = getDirectory().resolve(MAKEFILE);

    FileUtils.deleteQuietly(makefilePath.toFile());

    assertEquals(CommandResult.SUCCESSFUL, createCommand("reset").perform(Collections.singletonList(MAKEFILE)));
    assertTrue(makefilePath.toFile().exists());
  }
}
