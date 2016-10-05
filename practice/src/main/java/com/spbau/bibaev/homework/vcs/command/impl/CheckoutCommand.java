package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class CheckoutCommand extends RepositoryCommand {
  @SuppressWarnings("WeakerAccess")
  public CheckoutCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    String arg = args.get(0);
    if (repository.getCurrentBranch().getName().equals(arg)) {
      ConsoleColoredPrinter.println("Already on " + arg, ConsoleColoredPrinter.Color.YELLOW);
      return CommandResult.FAILED;
    }

    Branch branch = repository.getBranchByName(arg);

    Diff diff = repository.getProject().getDiff(repository.getCurrentBranch().getCommit().getRepositoryState());
    Collection<Path> newFiles = diff.getNewFiles();
    Collection<Path> modifiedFiles = diff.getModifiedFiles();
    if (modifiedFiles.size() > 0) {
      ConsoleColoredPrinter.println("RepositoryImpl contains uncommitted modified files. Commit/revert it.",
          ConsoleColoredPrinter.Color.RED);
      ConsoleColoredPrinter.printListOfFiles("Modified", ConsoleColoredPrinter.Color.YELLOW,
          FilesUtil.pathsToStrings(modifiedFiles));
      return CommandResult.FAILED;
    }

    if (branch == null) {
      final Commit commit = repository.getCommit(arg);
      if (commit == null) {
        ConsoleColoredPrinter.println(String.format("Branch or commit with name \"%s\" not found", args),
            ConsoleColoredPrinter.Color.RED);
        return CommandResult.FAILED;
      }
      repository.checkout(commit);
    } else {
      repository.checkout(branch);
    }

    return CommandResult.SUCCESSFUL;
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
    return "checkout branch_name|revision_hash";
  }
}
