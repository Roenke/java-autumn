package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public abstract class RepositoryCommand extends CommandBase {
  public RepositoryCommand(@NotNull File directory) {
    super(directory);
  }

  /** Implement with precondition that repository initialize
   * @param args command line args
   */
  @Override
  protected void performImpl(@NotNull List<String> args) throws RepositoryException {
    try {
      Repository rep = Repository.open(ourDirectory);
      perform(args, rep);

    } catch (RepositoryOpeningException e) {
      ConsoleColoredPrinter.println("Could not open repository, probably it corrupted", ConsoleColoredPrinter.RED);
    }
  }

  protected abstract void perform(@NotNull List<String> args, @NotNull Repository repository) throws RepositoryException;
}
