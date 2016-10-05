package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryIndex;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CommitCommand extends RepositoryCommand {
  @SuppressWarnings("WeakerAccess")
  public CommitCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    RepositoryIndex index = repository.getIndex();
    if (index.added().size() == 0 && index.removed().size() == 0) {
      ConsoleColoredPrinter.println("Nothing to commit. Use add/rm commands.", ConsoleColoredPrinter.Color.RED);
      return CommandResult.FAILED;
    }

    repository.commitChanges(args.size() > 0 ? args.get(0) : "");
    ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.Color.GREEN);
    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "commit [message]";
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }
}
