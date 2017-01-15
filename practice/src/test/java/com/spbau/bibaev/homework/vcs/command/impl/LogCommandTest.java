package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class LogCommandTest extends RepositoryTestCase {
  @Test
  public void noSideEffects() throws IOException {
    Repository repository = openRepository();
    repository.commitChanges("commit");

    final Repository before = openRepository();
    new LogCommand(myRule.getRoot().toPath()).perform(Collections.emptyList());
    final Repository after = openRepository();

    checkStateNotChanged(before, after);
  }
}
