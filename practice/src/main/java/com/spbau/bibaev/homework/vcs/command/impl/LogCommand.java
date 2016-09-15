package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogCommand extends RepositoryCommand {
  public LogCommand(@NotNull Repository repository) {
    super(repository);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) {

  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
