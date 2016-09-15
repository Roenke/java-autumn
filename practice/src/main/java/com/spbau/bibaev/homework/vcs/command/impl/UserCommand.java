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
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) {
    Metadata metadata = repository.getMetadata();
    if(metadata == null) {
      ConsoleColoredPrinter.println("Metadata not found", ConsoleColoredPrinter.RED);
      return;
    }

    if (args.size() == 0) {
      performShowCurrentUser(metadata);
    } else {
      metadata.userName = args.get(0);
      repository.setMetadata(metadata);
      repository.saveAll();
    }
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }

  private void performShowCurrentUser(@NotNull Metadata metadata) {
    ConsoleColoredPrinter.println(metadata.userName, ConsoleColoredPrinter.GREEN);
  }

  private void performSetUsername(@NotNull Repository repository, @NotNull Metadata meta, @NotNull String newName) {
    meta.userName = newName;
    repository.setMetadata(meta);

  }
}
