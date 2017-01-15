package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class AddCommandTest extends RepositoryTestCase {
  @Test
  public void simpleAddFile() throws IOException {
    final Command command = createCommand("add");
    CommandResult result = command.perform(Collections.singletonList(MAKEFILE));
    assertEquals(result, CommandResult.SUCCESSFUL);

    final Repository repository = openRepository();
    final Collection<String> added = repository.getIndex().added();
    assertEquals(1, added.size());
  }

  @Test
  public void doubleAddFailed() {
    final Command command = createCommand("add");
    CommandResult result = command.perform(Collections.singletonList(MAKEFILE));
    assertEquals(result, CommandResult.SUCCESSFUL);

    @Nullable final Command addCommand = createCommand("add");

    result = addCommand.perform(Collections.singletonList(MAKEFILE));
    assertEquals(CommandResult.FAILED, result);
  }

  @Test
  public void fewFiles() throws IOException {
    final Command command = createCommand("add");
    CommandResult result = command.perform(Arrays.asList(MAKEFILE, FILE));
    assertEquals(result, CommandResult.SUCCESSFUL);

    final Repository repository = openRepository();
    assertEquals(2, repository.getIndex().added().size());
  }
}