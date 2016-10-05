package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class RmCommandTest extends RepositoryTestCase {
  @Test
  public void rmUnknownFile() {
    assertEquals(CommandResult.FAILED, createCommand("rm").perform(Collections.singletonList(MAKEFILE + FILE)));
  }

  @Test
  public void rmExistedFile() throws IOException {
    assertEquals(CommandResult.SUCCESSFUL, createCommand("rm").perform(Collections.singletonList(MAKEFILE)));
    final Repository repository = openRepository();
    assertFalse(repository.getWorkingDirectory().getAllFiles().stream()
        .filter(x -> x.getFileName().toString().equals(MAKEFILE)).findFirst().isPresent());
  }

  @Test
  public void addFileToDeleteIndex() throws IOException {
    assertEquals(CommandResult.SUCCESSFUL ,createCommand("add").perform(Collections.singletonList(MAKEFILE)));
    createCommand("commit").perform(Collections.singletonList("message"));
    createCommand("rm").perform(Collections.singletonList(MAKEFILE));

    final Repository after = openRepository();
    final Collection<String> removed = after.getIndex().removed();
    assertFalse(removed.isEmpty());
    assertTrue(removed.contains(MAKEFILE));
  }

  @Test
  public void rmAddedFileFromIndex() throws IOException {
    assertEquals(CommandResult.SUCCESSFUL, createCommand("add").perform(Collections.singletonList(MAKEFILE)));
    final Repository before = openRepository();
    assertFalse(before.getIndex().added().isEmpty());

    assertEquals(CommandResult.SUCCESSFUL, createCommand("rm").perform(Collections.singletonList(MAKEFILE)));
    final Repository after = openRepository();
    assertTrue(after.getIndex().added().isEmpty());
    assertFalse(after.getIndex().removed().isEmpty());
  }
}