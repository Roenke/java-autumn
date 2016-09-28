package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RevertCommand extends RepositoryCommand {
  @SuppressWarnings("WeakerAccess")
  public RevertCommand(@NotNull Path directory) {
    super(directory);
  }

  @NotNull
  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    repository.checkout(repository.getCurrentBranch());
    ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.Color.GREEN);
    return CommandResult.SUCCESSFUL;
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
