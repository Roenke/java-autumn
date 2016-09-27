package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class LogCommandTest extends RepositoryTestCase {
  @Test
  public void noSideEffects() throws IOException {
    Repository repository = openRepository();
    repository.commitChanges("commit");

    final Repository before = openRepository();
    new LogCommand(myRule.getRoot().toPath()).perform(Collections.emptyList());
    final Repository after = openRepository();

    assertEquals(before.getCurrentBranch().getName(), after.getCurrentBranch().getName());
    assertEquals(before.getCurrentBranch().getLastRevision().getDate(),
        after.getCurrentBranch().getLastRevision().getDate());
    assertEquals(before.getProject().getAllFiles().size(), after.getProject().getAllFiles().size());
    assertEquals(before.getCurrentBranch().getRevisions().size(), after.getCurrentBranch().getRevisions().size());
  }
}
