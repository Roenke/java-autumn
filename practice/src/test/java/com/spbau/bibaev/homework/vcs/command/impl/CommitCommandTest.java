package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class CommitCommandTest extends RepositoryTestCase {
  @Test
  public void simpleCommitAll() throws IOException {
    String commitMessage = "commit it";
    new CommitCommand(myRule.getRoot().toPath()).perform(Collections.singletonList(commitMessage));

    final Repository repository = openRepository();
    assertNotNull(repository);

    assertEquals(commitMessage, repository.getCurrentBranch().getLastRevision().getMessage());
  }
}