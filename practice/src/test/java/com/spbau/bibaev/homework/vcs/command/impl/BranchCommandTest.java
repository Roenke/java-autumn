package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class BranchCommandTest extends RepositoryTestCase {
  @Test
  public void listBranchesTest() throws IOException {
    Repository repository = openRepository();
    assertNotNull(repository);
    Branch currentBranch = repository.getCurrentBranch();

    Command command = new BranchCommand(repository.getProject().getRootDirectory());
    command.perform(Collections.emptyList());

    Repository updated = openRepository();
    assertEquals(currentBranch.getName(), updated.getCurrentBranch().getName());
  }

  @Test
  public void newBranchTest() throws IOException {
    String branchName = "develop";

    Repository repositoryBefore = openRepository();
    assertNotNull(repositoryBefore);
    String currentBranchBefore = repositoryBefore.getCurrentBranch().getName();
    assertNull(repositoryBefore.getBranchByName(branchName));

    Command command = new BranchCommand(myRule.getRoot().toPath());
    command.perform(Collections.singletonList(branchName));

    Repository updatedRepository = openRepository();
    assertNotNull(updatedRepository);
    assertEquals(updatedRepository.getCurrentBranch().getName(), currentBranchBefore);

    Branch newBranch = updatedRepository.getBranchByName(branchName);
    assertNotNull(newBranch);
    assertEquals("Number of revision must be same in HEAD and new branch",
        repositoryBefore.getCurrentBranch().getRevisions().size(),
        newBranch.getRevisions().size());
  }
}