package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class UserCommand extends RepositoryCommand {
  public UserCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    if (args.size() == 0) {
      ConsoleColoredPrinter.println(repository.getUserName(), ConsoleColoredPrinter.Color.GREEN);
    } else {
      repository.setUserName(args.get(0));
    }

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "user [new_name]";
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }
}
