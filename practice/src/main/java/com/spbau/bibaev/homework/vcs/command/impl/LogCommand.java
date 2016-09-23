package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.impl.BranchImpl;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.impl.RevisionImpl;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.*;

public class LogCommand extends RepositoryCommand {
  public LogCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull RepositoryImpl repository) throws RepositoryException {
    BranchImpl currentBranch = repository.getCurrentBranch();
    List<RevisionImpl> revisions = currentBranch.getRevisions();
    for(RevisionImpl revision : revisions) {
      ConsoleColoredPrinter.println("commit: " + revision.getHash(), GREEN);
      ConsoleColoredPrinter.println("user: " + revision.getAuthor(), WHITE);
      ConsoleColoredPrinter.println("date: " + revision.getDate(), WHITE);
      ConsoleColoredPrinter.println("", WHITE);
      ConsoleColoredPrinter.println("\t\t" + revision.getMessage() , WHITE);
      ConsoleColoredPrinter.println("", WHITE);
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
