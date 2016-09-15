package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandBase implements Command {
  private final Repository myRepository;

  public CommandBase(@NotNull Repository repository) {
    myRepository = repository;
  }

  @Override
  public void perform(@NotNull List<String> args) {
    int argCount = args.size();
    if(getMinArgCount() <= argCount && argCount <= getMaxArgCount()) {
      performImpl(args);
    }
  }

  protected abstract void performImpl(@NotNull List<String> args);

  @NotNull
  protected Repository getRepository() {
    return myRepository;
  }

  protected int getMinArgCount() {
    return 0;
  }

  protected int getMaxArgCount() {
    return Integer.MAX_VALUE;
  }
}
