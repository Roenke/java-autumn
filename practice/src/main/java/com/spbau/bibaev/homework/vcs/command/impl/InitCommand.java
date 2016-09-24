package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryFacade;
import com.spbau.bibaev.homework.vcs.command.CommandBase;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class InitCommand extends CommandBase {
  public InitCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected void performImpl(@NotNull List<String> args) throws IOException {
    if(RepositoryFacade.getInstance().openRepository(ourDirectory) != null) {
      ConsoleColoredPrinter.println("Repository in " + ourDirectory.toString() + " already exists");
    }

    Repository repository = RepositoryFacade.getInstance().initRepository(ourDirectory);
    if(repository != null) {
      ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.Color.GREEN);
    }
  }

  @Override
  protected String getUsage() {
    return "init";
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
