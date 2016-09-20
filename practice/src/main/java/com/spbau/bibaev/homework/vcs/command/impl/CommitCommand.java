package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class CommitCommand extends RepositoryCommand {
  public CommitCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws RepositoryException {
    repository.commitChanges(args.size() > 0 ? args.get(0) : "");
    ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.GREEN);
  }

  @Override
  protected String getUsage() {
    return "commit [message]";
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }
}
