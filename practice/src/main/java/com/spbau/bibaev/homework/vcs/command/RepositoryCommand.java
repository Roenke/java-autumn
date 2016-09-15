package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RepositoryCommand extends CommandBase {
  public RepositoryCommand(@NotNull Repository repository) {
    super(repository);
  }

  @Override
  public final void perform(@NotNull List<String> args) {
    Repository rep = getRepository();
    if (!rep.isInitialized()) {
      System.out.println("Repository must be initialized. Use: my_cvs init");
    } else {
      performImpl(args);
    }
  }

  public abstract void performImpl(@NotNull List<String> args);
}
