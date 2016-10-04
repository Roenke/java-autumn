package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class StatusCommandTest extends RepositoryTestCase {
  @Test
  public void noSideEffects() throws IOException {
    Repository repository = openRepository();
    repository.commitChanges("abc");

    Repository before = openRepository();
    new StatusCommand(myRule.getRoot().toPath()).perform(Collections.emptyList());
    Repository after = openRepository();

    checkStateNotChanged(before, after);
  }
}