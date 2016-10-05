package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import org.junit.Test;

import java.io.IOException;
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
    final Repository repository = openRepository();

    repository.commitChanges(commitMessage);
    Commit prevRevision = repository.getCurrentBranch().getCommit();
    addFile("newFile.cpp", "hello");
    repository.commitChanges(commitMessage + "2");
    Commit lastRevision = repository.getCurrentBranch().getCommit();
    String oldBranchName = repository.getCurrentBranch().getName();

    new CheckoutCommand(myRule.getRoot().toPath()).perform(Collections.singletonList(lastRevision.getMeta().getHashcode()));
    Repository updated = RepositoryImpl.openRepository(myRule.getRoot().toPath());
    assertNotNull(updated);
    assertNotEquals(oldBranchName, updated.getCurrentBranch().getName());
    assertEquals(prevRevision.getMeta().getDate(), updated.getCurrentBranch().getCommit().getMeta().getDate());
  }
}
