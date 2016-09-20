package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class UserCommand extends RepositoryCommand {
  public UserCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws RepositoryException {
    if (args.size() == 0) {
      ConsoleColoredPrinter.println(repository.getUserName(), ConsoleColoredPrinter.GREEN);
    } else {
      repository.setUserName(args.get(0));
      repository.save();
    }
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
