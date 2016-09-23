package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.command.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class InitCommand extends CommandBase {
  public InitCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void performImpl(@NotNull List<String> args) throws RepositoryIOException {
    RepositoryImpl.createNewRepository(ourDirectory);
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
