package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class CheckoutCommand extends RepositoryCommand {
  public CheckoutCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    String arg = args.get(0);
    if(repository.getCurrentBranch().getName().equals(arg)) {
      ConsoleColoredPrinter.println("Already on " + arg, ConsoleColoredPrinter.Color.YELLOW);
      return;
    }

    Branch branch = repository.getBranchByName(arg);
    Diff diff = repository.getProject().getDiff(repository.getCurrentBranch().getLastRevision());
    Collection<Path> newFiles = diff.getNewFiles();
    Collection<Path> modifiedFiles = diff.getModifiedFiles();
    if (newFiles.size() + modifiedFiles.size() > 0) {
      ConsoleColoredPrinter.println("RepositoryImpl contains uncommitted files. Commit/revert it.",
          ConsoleColoredPrinter.Color.RED);
      ConsoleColoredPrinter.printListOfFiles("New", ConsoleColoredPrinter.Color.RED, FilesUtil.pathsToStrings(newFiles));
      ConsoleColoredPrinter.printListOfFiles("Modified", ConsoleColoredPrinter.Color.YELLOW,
          FilesUtil.pathsToStrings(modifiedFiles));
      return;
    }

    if (branch == null) {
      Revision revision = repository.getCurrentBranch().getRevisions().stream()
          .filter(rev -> rev.getHash().equals(arg)).findFirst().orElse(null);
      if (revision == null) {
        ConsoleColoredPrinter.println("Such branch or revision not found", ConsoleColoredPrinter.Color.RED);
        return;
      }

      repository.checkout(revision);
    } else {
      repository.checkout(branch);
    }
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
