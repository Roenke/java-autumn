package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class CheckoutCommandTest extends RepositoryTestCase {
  @Test
  public void checkoutToExistedBranch() throws IOException {
    final String branchName = "develop";

    Repository before = openRepository();
    String oldCommitId = before.getCurrentBranch().getCommit().getMeta().getId();

    assertEquals(CommandResult.SUCCESSFUL , createCommand("branch").perform(Collections.singletonList(branchName)));

    assertEquals(CommandResult.SUCCESSFUL , createCommand("add").perform(Collections.singletonList(MAKEFILE)));
    assertEquals(CommandResult.SUCCESSFUL , createCommand("commit").perform(Collections.singletonList(branchName)));

    Repository repository = openRepository();
    assertNotEquals(branchName, repository.getCurrentBranch().getName());
    assertEquals(CommandResult.SUCCESSFUL, createCommand("checkout").perform(Collections.singletonList(branchName)));
    Repository after = openRepository();

    assertEquals(branchName, after.getCurrentBranch().getName());
    assertEquals(oldCommitId, after.getCurrentBranch().getCommit().getMeta().getId());
  }

  @Test
  public void checkoutToRevision() throws IOException {
    String commitMessage = "message";

    final Repository before = openRepository();
    final String commitId = before.getCurrentBranch().getCommit().getMeta().getId();
    final String oldBranchName = before.getCurrentBranch().getName();

    createCommand("add").perform(Arrays
        .asList(MAKEFILE, FILE, NESTED_FILE1, NESTED_FILE2, NESTED_FILE3, NESTED_NESTED_FILE));
    final Repository repository = openRepository();
    assertEquals(6, repository.getIndex().added().size());
    createCommand("commit").perform(Collections.singletonList(commitMessage));
    assertEquals(CommandResult.SUCCESSFUL, createCommand("checkout").perform(Collections.singletonList(commitId)));

    final Repository after = openRepository();
    assertNotEquals(oldBranchName, after.getCurrentBranch().getName());
    assertEquals(0, repository.getWorkingDirectory().getAllFiles().size());
  }
}
