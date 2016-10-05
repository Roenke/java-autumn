package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryIndex;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.*;

public class CommitCommandTest extends RepositoryTestCase {
  @Test
  public void commitWithEmptyIndex() throws IOException {
    Repository before = openRepository();
    RepositoryIndex index = before.getIndex();
    assertEquals(0, index.added().size());
    assertEquals(0, index.removed().size());

    assertEquals(CommandResult.FAILED, createCommand("commit").perform(Collections.singletonList("message")));
  }

  @Test
  public void simpleCommitFile() throws IOException {
    String commitMessage = "commit it";

    createCommand("add").perform(Collections.singletonList(MAKEFILE));
    createCommand("commit").perform(Collections.singletonList(commitMessage));

    Repository after = openRepository();

    assertEquals(commitMessage, after.getCurrentBranch().getCommit().getMeta().getMessage());
    assertEquals(after.getUserName(), after.getCurrentBranch().getCommit().getMeta().getAuthor());
  }
}
