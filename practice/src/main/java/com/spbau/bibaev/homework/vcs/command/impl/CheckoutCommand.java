package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.impl.BranchImpl;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.repository.impl.RevisionImpl;
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
  protected void perform(@NotNull List<String> args, @NotNull RepositoryImpl repository) throws RepositoryException {
    String arg = args.get(0);
    if(repository.getCurrentBranch().getName().equals(arg)) {
      ConsoleColoredPrinter.println("Already on " + arg, ConsoleColoredPrinter.Color.YELLOW);
      return;
    }

    BranchImpl branch = repository.getBranchByName(arg);
    Diff diff = repository.getProject().diffWithRevision(repository.getCurrentBranch().getLastRevision());
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
      RevisionImpl revision = repository.getRevisionByName(arg);
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
