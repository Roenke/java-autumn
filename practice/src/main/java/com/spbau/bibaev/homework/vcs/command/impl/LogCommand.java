package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.Repository;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogCommand extends RepositoryCommand {
  public LogCommand(@NotNull Repository repository) {
    super(repository);
  }

  @Override
  public void performImpl(@NotNull List<String> args) {
    Repository repository = getRepository();
  }
}
