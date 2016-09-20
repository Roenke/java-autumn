package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class CheckoutCommand extends RepositoryCommand {
  public CheckoutCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) {
//    String arg = args.get(0);
//    Branch branch = repository.getBranchByName(arg);
  }

  @Override
  protected int getMinArgCount() {
    return 1;
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }

  @Override
  protected String getUsage() {
    return "checkout branch|revision";
  }
}
