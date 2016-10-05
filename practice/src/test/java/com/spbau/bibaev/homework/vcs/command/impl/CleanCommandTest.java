package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CleanCommandTest extends RepositoryTestCase {
  @Test
  public void simpleClean() throws IOException {
    final Repository before = openRepository();
    Diff diff = before.getWorkingDirectory().getDiff(before.getCurrentBranch().getCommit().getRepositoryState());
    assertNotEquals(0, diff.getNewFiles().size());
    assertEquals(0, diff.getDeletedFiles().size());
    assertEquals(0, diff.getModifiedFiles().size());

    final CommandResult result = createCommand("clean").perform(Collections.emptyList());
    assertEquals(CommandResult.SUCCESSFUL, result);

    final Repository after = openRepository();
    diff = after.getWorkingDirectory().getDiff(after.getCurrentBranch().getCommit().getRepositoryState());
    assertEquals(0, diff.getNewFiles().size());
    assertEquals(0, diff.getDeletedFiles().size());
    assertEquals(0, diff.getModifiedFiles().size());
  }
}
