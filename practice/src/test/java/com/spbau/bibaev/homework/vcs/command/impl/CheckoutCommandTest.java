package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class CheckoutCommandTest extends RepositoryTestCase {
  @Test
  public void checkoutRoExistedBranch() throws IOException {
    final String branchName = "develop";

    final Repository repository = openRepository();
    assertNotNull(repository);

    repository.createNewBranch(branchName);
    assertNotNull(repository.getBranchByName(branchName));
    assertNotEquals(repository.getCurrentBranch().getName(), branchName);

    final CheckoutCommand checkoutCommand = new CheckoutCommand(myRule.getRoot().toPath());
    checkoutCommand.perform(Collections.singletonList(branchName), repository);

    assertEquals(branchName, repository.getCurrentBranch().getName());
    assertEquals(openRepository().getCurrentBranch().getName(), branchName);
  }

  @Test
  public void checkoutToRevision() throws IOException {
    String commitMessage = "message";
    final Repository repository = openRepository();

    repository.commitChanges(commitMessage);
    Revision prevRevision = repository.getCurrentBranch().getLastRevision();
    addFile("newFile.cpp", "hello");
    repository.commitChanges(commitMessage + "2");
    Revision lastRevision = repository.getCurrentBranch().getLastRevision();
    String oldBranchName = repository.getCurrentBranch().getName();

    new CheckoutCommand(myRule.getRoot().toPath()).perform(Collections.singletonList(lastRevision.getHash()));
    Repository updated = openRepository();
    assertNotNull(updated);
    assertNotEquals(oldBranchName, updated.getCurrentBranch().getName());
    assertEquals(prevRevision.getDate(), updated.getCurrentBranch().getLastRevision().getDate());
  }
}