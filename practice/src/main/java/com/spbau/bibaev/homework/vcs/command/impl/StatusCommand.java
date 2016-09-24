package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.*;
import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.printListOfFiles;

public class StatusCommand extends RepositoryCommand {
  public StatusCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Branch currentBranch = repository.getCurrentBranch();
    Revision lastRevision = currentBranch.getLastRevision();

    ConsoleColoredPrinter.println("On branch " + currentBranch.getName());
    ConsoleColoredPrinter.println("RevisionImpl: " + lastRevision.getHash(), ConsoleColoredPrinter.Color.GREEN);

    Diff diff = repository.getProject().getDiff(repository.getCurrentBranch().getLastRevision());
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
