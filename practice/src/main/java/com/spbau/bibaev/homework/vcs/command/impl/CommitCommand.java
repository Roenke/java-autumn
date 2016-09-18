package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
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
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) {
    try {
      repository.commitChanges(args.size() > 0 ? args.get(0) : "");
      ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.GREEN);
    } catch (RepositoryIOException e) {
      ConsoleColoredPrinter.println("Error occurred: " + e.getMessage(), ConsoleColoredPrinter.RED);
    }
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }
}
