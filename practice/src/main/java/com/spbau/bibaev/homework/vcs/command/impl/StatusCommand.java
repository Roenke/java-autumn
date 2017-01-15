package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.*;
import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.print;

public class StatusCommand extends RepositoryCommand {
  @SuppressWarnings("WeakerAccess")
  public StatusCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Branch currentBranch = repository.getCurrentBranch();
    Commit lastCommit = currentBranch.getCommit();

    ConsoleColoredPrinter.println("On branch " + currentBranch.getName());
    ConsoleColoredPrinter.println("Revision: " + lastCommit.getMeta().getHashcode(), ConsoleColoredPrinter.Color.GREEN);

    Diff diff = repository.getWorkingDirectory().getDiff(repository.getCurrentBranch().getCommit().getRepositoryState());
    print("New files", GREEN, FilesUtil.pathsToStrings(diff.getNewFiles()));
    print("Modified", YELLOW, FilesUtil.pathsToStrings(diff.getModifiedFiles()));
    print("Deleted", RED, FilesUtil.pathsToStrings(diff.getDeletedFiles()));

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "status";
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
