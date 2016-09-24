package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class RepositoryCommand extends CommandBase {
  public RepositoryCommand(@NotNull File directory) {
    super(directory);
  }

  /**
   * Implement with precondition that repository initialized
   *
   * @param args command line args
   */
  @Override
  protected void performImpl(@NotNull List<String> args) throws IOException {
    try {
      RepositoryImpl rep = RepositoryImpl.open(ourDirectory);
      perform(args, rep);
    } catch (IOException e) {
      ConsoleColoredPrinter.println("Error occurred: " + e.getMessage(), ConsoleColoredPrinter.Color.RED);
    }
  }

  protected abstract void perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException;
}
