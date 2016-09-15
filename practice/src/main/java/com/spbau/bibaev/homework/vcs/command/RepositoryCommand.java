package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RepositoryCommand extends CommandBase {
  public RepositoryCommand(@NotNull Repository repository) {
    super(repository);
  }

  /** Implement with precondition that repository initialize
   * @param args command line args
   */
  @Override
  protected void performImpl(@NotNull List<String> args) {
    Repository rep = getRepository();
    if (!rep.isInitialized()) {
      System.out.println("Repository must be initialized. Use: my_cvs init");
    } else {
      perform(args, rep);
    }
  }

  protected abstract void perform(@NotNull List<String> args, @NotNull Repository repository);
}
