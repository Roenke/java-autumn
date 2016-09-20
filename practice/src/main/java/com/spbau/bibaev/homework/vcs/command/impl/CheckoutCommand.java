package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.Branch;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.repository.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.Diff;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class CheckoutCommand extends RepositoryCommand {
  public CheckoutCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws RepositoryException {
    String arg = args.get(0);
    if(repository.getCurrentBranch().getName().equals(arg)) {
      ConsoleColoredPrinter.println("Already on " + arg, ConsoleColoredPrinter.YELLOW);
      return;
    }

    Branch branch = repository.getBranchByName(arg);
    Diff diff = repository.getProject().diffWithRevision(repository.getCurrentBranch().getLastRevision());
    Collection<Path> newFiles = diff.getNewFiles();
    Collection<Path> modifiedFiles = diff.getModifiedFiles();
    if (newFiles.size() + modifiedFiles.size() > 0) {
      ConsoleColoredPrinter.println("Repository contains uncommitted files. Commit/revert it.",
          ConsoleColoredPrinter.RED);
      ConsoleColoredPrinter.printListOfFiles("New", ConsoleColoredPrinter.RED, FilesUtil.pathsToStrings(newFiles));
      ConsoleColoredPrinter.printListOfFiles("Modified", ConsoleColoredPrinter.YELLOW,
          FilesUtil.pathsToStrings(modifiedFiles));
      return;
    }

    if (branch == null) {
      Revision revision = repository.getRevisionByName(arg);
      if (revision == null) {
        ConsoleColoredPrinter.println("Such branch or revision not found", ConsoleColoredPrinter.RED);
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
