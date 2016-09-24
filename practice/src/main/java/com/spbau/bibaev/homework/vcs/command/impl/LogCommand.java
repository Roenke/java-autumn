package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.GREEN;
import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.WHITE;

public class LogCommand extends RepositoryCommand {
  public LogCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Branch currentBranch = repository.getCurrentBranch();
    List<Revision> revisions = currentBranch.getRevisions();
    for(Revision revision : revisions) {
      ConsoleColoredPrinter.println("commit: " + revision.getHash(), GREEN);
      ConsoleColoredPrinter.println("user: " + revision.getAuthorName(), WHITE);
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
