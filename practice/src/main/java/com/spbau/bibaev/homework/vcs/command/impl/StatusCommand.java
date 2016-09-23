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
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.*;
import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.printListOfFiles;

public class StatusCommand extends RepositoryCommand {
  public StatusCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull RepositoryImpl repository) throws RepositoryException {
    BranchImpl currentBranch = repository.getCurrentBranch();
    RevisionImpl lastRevision = currentBranch.getLastRevision();

    ConsoleColoredPrinter.println("On branch " + currentBranch.getName());
    ConsoleColoredPrinter.println("RevisionImpl: " + lastRevision.getHash(), ConsoleColoredPrinter.Color.GREEN);

    Diff diff = repository.getProject().diffWithRevision(repository.getCurrentBranch().getLastRevision());
    printListOfFiles("New files", GREEN, FilesUtil.pathsToStrings(diff.getNewFiles()));
    printListOfFiles("Modified", YELLOW, FilesUtil.pathsToStrings(diff.getModifiedFiles()));
    printListOfFiles("Deleted", RED, FilesUtil.pathsToStrings(diff.getDeletedFiles()));
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
