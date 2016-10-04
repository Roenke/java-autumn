package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import org.jetbrains.annotations.NotNull;
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

    Command command = createCommand("branch");
    command.perform(Collections.emptyList());

    Repository updated = openRepository();
    assertEquals(currentBranch.getName(), updated.getCurrentBranch().getName());
  }

  @Test
  public void newBranchTest() throws IOException {
    String branchName = "develop";

    final Repository before = openRepository();
    String beforeBranchName = before.getCurrentBranch().getName();
    assertNotEquals(branchName, beforeBranchName);

    @NotNull final Command branchCommand = createCommand("branch");
    branchCommand.perform(Collections.singletonList(branchName));

    final Repository after = openRepository();
    assertNotEquals(branchName, after.getCurrentBranch().getName());
    assertEquals(beforeBranchName, after.getCurrentBranch().getName());
    assertNotNull(after.getBranchByName(branchName));
  }
}