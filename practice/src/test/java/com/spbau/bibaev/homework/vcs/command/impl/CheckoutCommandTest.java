package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
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

    final Repository repository = RepositoryImpl.openRepository(myRule.getRoot().toPath());
    assertNotNull(repository);

    repository.createNewBranch(branchName, repository.getCurrentBranch().getCommit());
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
    final Repository repository = RepositoryImpl.openRepository(myRule.getRoot().toPath());

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