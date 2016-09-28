package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class CleanCommand extends RepositoryCommand {
  public CleanCommand(@NotNull Path directory) {
    super(directory);
  }

  @NotNull
  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Revision lastRevision = repository.getCurrentBranch().getLastRevision();
    Collection<Path> newFiles = repository.getProject().getDiff(lastRevision).getNewFiles();
    if (newFiles.size() == 0) {
      ConsoleColoredPrinter.println("Already cleaned");
    } else {
      for (Path path : newFiles) {
        FileUtils.deleteQuietly(path.toFile());
        File[] siblings = path.getParent().toFile().listFiles();
        if (siblings != null && siblings.length == 0) {
          FileUtils.deleteQuietly(path.getParent().toFile());
        }
      }
    }

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }

  @Override
  protected String getUsage() {
    return "clean";
  }
}
