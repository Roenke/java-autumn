package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class BranchCommandTest extends RepositoryTestCase {
  @Test
  public void listBranchesTest() throws IOException {
    Repository before = openRepository();
    assertNotNull(before);
    Branch currentBranch = before.getCurrentBranch();

    Command command = createCommand("branch");
    command.perform(Collections.emptyList());

    Repository after = openRepository();

    assertEquals(currentBranch.getName(), after.getCurrentBranch().getName());
    checkStateNotChanged(before, after);
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