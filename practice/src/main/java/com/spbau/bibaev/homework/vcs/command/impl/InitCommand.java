package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.command.CommandBase;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class InitCommand extends CommandBase {
  public InitCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void performImpl(@NotNull List<String> args) throws RepositoryIOException {
    Repository.createNewRepository(ourDirectory);
  }

  @Override
  protected String getUsage() {
    return "init";
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
