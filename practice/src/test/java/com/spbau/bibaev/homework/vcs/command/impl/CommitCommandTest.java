package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryIndex;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

  @Test
  public void commitFewFiles() throws IOException {
    String message1 = "first";
    String message2 = "second";

    Repository before = openRepository();
    String branchBefore = before.getCurrentBranch().getName();

    createCommand("add").perform(Collections.singletonList(MAKEFILE));
    createCommand("commit").perform(Collections.singletonList(message1));

    createCommand("add").perform(Arrays.asList(FILE, NESTED_FILE1));
    createCommand("commit").perform(Collections.singletonList(message2));

    Repository after = openRepository();
    assertEquals(branchBefore, after.getCurrentBranch().getName());
    assertEquals(message2, after.getCurrentBranch().getCommit().getMeta().getMessage());
    assertEquals(message1, after.getCurrentBranch().getCommit().getParents().get(0).getMeta().getMessage());

    Commit commit = after.getCurrentBranch().getCommit();
    assertEquals(2, commit.getAddedFiles().size());
    assertEquals(0, commit.getModifiedFiles().size());
    assertEquals(0, commit.getDeletedFiles().size());

    assertEquals(1, commit.getParents().size());
    Commit parent = commit.getMainParent();
    assertNotNull(parent);
    assertEquals(1, parent.getAddedFiles().size());
    assertEquals(0, parent.getModifiedFiles().size());
    assertEquals(0, parent.getDeletedFiles().size());
  }
}
