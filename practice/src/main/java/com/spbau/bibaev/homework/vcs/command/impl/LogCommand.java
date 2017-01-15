package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.CommitMeta;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.GREEN;
import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.WHITE;

public class LogCommand extends RepositoryCommand {
  public LogCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Branch currentBranch = repository.getCurrentBranch();
    Commit currentCommit = currentBranch.getCommit();
    LinkedList<Commit> commits = new LinkedList<>();
    while (currentCommit != null) {
      commits.addFirst(currentCommit);
      currentCommit = currentCommit.getMainParent();
    }

    for(Commit commit : commits) {
      CommitMeta meta = commit.getMeta();
      ConsoleColoredPrinter.println("commit: " + meta.getHashcode(), GREEN);
      ConsoleColoredPrinter.println("user: " + meta.getAuthor(), WHITE);
      ConsoleColoredPrinter.println("date: " + meta.getDate(), WHITE);
      ConsoleColoredPrinter.println("", WHITE);
      ConsoleColoredPrinter.println("\t\t" + meta.getMessage() , WHITE);
      ConsoleColoredPrinter.println("", WHITE);
    }

    return CommandResult.SUCCESSFUL;
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
