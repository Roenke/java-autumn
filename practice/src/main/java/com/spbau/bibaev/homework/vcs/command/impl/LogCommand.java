package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.Branch;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class LogCommand extends RepositoryCommand {
  public LogCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws RepositoryException {
    Branch currentBranch = repository.getCurrentBranch();
    List<Revision> revisions = currentBranch.getRevisions();
    for(Revision revision : revisions) {
      ConsoleColoredPrinter.println("commit: " + revision.getHash(), ConsoleColoredPrinter.GREEN);
      ConsoleColoredPrinter.println("user: " + revision.getAuthor(), ConsoleColoredPrinter.WHITE);
      ConsoleColoredPrinter.println("date: " + revision.getDate(), ConsoleColoredPrinter.WHITE);
      ConsoleColoredPrinter.println("", ConsoleColoredPrinter.WHITE);
      ConsoleColoredPrinter.println("\t\t" + revision.getMessage() , ConsoleColoredPrinter.WHITE);
      ConsoleColoredPrinter.println("", ConsoleColoredPrinter.WHITE);
    }
  }

  @Override
  protected String getUsage() {
    return "log";
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
