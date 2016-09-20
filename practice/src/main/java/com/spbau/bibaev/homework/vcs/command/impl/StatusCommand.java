package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.Branch;
import com.spbau.bibaev.homework.vcs.repository.Project;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.repository.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.Diff;
import com.spbau.bibaev.homework.vcs.util.FileState;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.printListOfFiles;

public class StatusCommand extends RepositoryCommand {
  public StatusCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws RepositoryException {
    Branch currentBranch = repository.getCurrentBranch();
    Revision lastRevision = currentBranch.getLastRevision();

    ConsoleColoredPrinter.println("On branch " + currentBranch.getName());
    ConsoleColoredPrinter.println("Revision: " + lastRevision.getHash(), ConsoleColoredPrinter.GREEN);

    Diff diff = repository.getProject().diffWithRevision(repository.getCurrentBranch().getLastRevision());
    printListOfFiles("New files", ConsoleColoredPrinter.GREEN, FilesUtil.pathsToStrings(diff.getNewFiles()));
    printListOfFiles("Modified", ConsoleColoredPrinter.YELLOW, FilesUtil.pathsToStrings(diff.getModifiedFiles()));
    printListOfFiles("Deleted", ConsoleColoredPrinter.RED, FilesUtil.pathsToStrings(diff.getDeletedFiles()));
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
