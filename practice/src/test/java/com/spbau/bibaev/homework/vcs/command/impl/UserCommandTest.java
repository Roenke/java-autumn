package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.RepositoryTestCase;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class UserCommandTest extends RepositoryTestCase {
  @Test
  public void changeUsername() throws IOException {
    String newUsername = "foo";
    createCommand("user").perform(Collections.singletonList(newUsername));
    final Repository repository = openRepository();
    assertEquals(newUsername, repository.getUserName());
  }
}