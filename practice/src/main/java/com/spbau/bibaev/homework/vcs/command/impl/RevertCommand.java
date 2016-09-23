package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class RevertCommand extends RepositoryCommand {
  public RevertCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull RepositoryImpl repository) throws RepositoryException {
    repository.checkout(repository.getCurrentBranch());
    ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.Color.GREEN);
  }

  @Override
  protected String getUsage() {
    return "revert";
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
