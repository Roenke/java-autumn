package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.command.CommandBase;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InitCommand extends CommandBase {
  public InitCommand(@NotNull Repository repository) {
    super(repository);
  }

  @Override
  protected void performImpl(@NotNull List<String> args) {
    Repository repository = getRepository();
    if (repository.isInitialized()) {
      ConsoleColoredPrinter.println("Already initialized", ConsoleColoredPrinter.RED);
    } else {
      repository.initialize();
    }
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
