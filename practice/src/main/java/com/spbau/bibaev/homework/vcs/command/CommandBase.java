package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandBase {
  private final Repository myRepository;

  public CommandBase(@NotNull Repository repository) {
    myRepository = repository;
  }

  @NotNull
  protected Repository getRepository() {
    return myRepository;
  }

  public abstract void perform(@NotNull List<String> args);
}
