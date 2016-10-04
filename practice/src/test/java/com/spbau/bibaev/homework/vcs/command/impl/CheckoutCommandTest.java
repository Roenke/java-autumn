package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.Command;
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

    final Repository before = RepositoryImpl.openRepository(myRule.getRoot().toPath());
    assertNotNull(before);

    before.createNewBranch(branchName, before.getCurrentBranch().getCommit());
    assertNotNull(before.getBranchByName(branchName));
    assertNotEquals(before.getCurrentBranch().getName(), branchName);
    before.save();

    final Command checkoutCommand = createCommand("checkout");
    checkoutCommand.perform(Collections.singletonList(branchName));

    final Repository after = openRepository();
    assertEquals(branchName, after.getCurrentBranch().getName());
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