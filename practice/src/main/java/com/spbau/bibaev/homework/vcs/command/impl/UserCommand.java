package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.Metadata;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserCommand extends RepositoryCommand {
  public UserCommand(@NotNull Repository repository) {
    super(repository);
  }

  @Override
  public void performImpl(@NotNull List<String> args) {
    Metadata metadata = getRepository().getMetadata();
    if(metadata == null) {
      ConsoleColoredPrinter.println("Metadata not found", ConsoleColoredPrinter.RED);
      return;
    }

    if (args.size() == 0) {
        ConsoleColoredPrinter.println(metadata.userName, ConsoleColoredPrinter.GREEN);
    } else {
      metadata.userName = args.get(0);
      getRepository().setMetadata(metadata);
      getRepository().saveAll();
    }
  }
}
