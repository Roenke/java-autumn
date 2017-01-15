package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.MergeException;
import com.spbau.bibaev.homework.vcs.repository.api.*;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class MergeCommand extends RepositoryCommand {

  public MergeCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Branch srcBranch = repository.getBranchByName(args.get(0));

    if (srcBranch == null) {
      ConsoleColoredPrinter.println("Such branch not found", ConsoleColoredPrinter.Color.RED);
      return CommandResult.FAILED;
    }

    try {
      final Commit merge = repository.merge(srcBranch.getCommit(), String.format("Merge with %s", srcBranch.getCommit().getMeta().getId()),
          new MyUserConflictResolver());
      if (merge == null) {
        return CommandResult.FAILED;
      }
    } catch (MergeException me) {
      ConsoleColoredPrinter.println("Merge failed", ConsoleColoredPrinter.Color.RED);
      return CommandResult.FAILED;
    }

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "merge branch_name";
  }

  @Override
  protected int getMinArgCount() {
    return 1;
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }

  private static class MyUserConflictResolver implements MergeConflictResolver {
    private final Scanner myScanner = new Scanner(System.in);

    @Override
    public MergeResolvingResult resolve(@NotNull Path file, @NotNull Repository repository) {
      ConsoleColoredPrinter.println("conflict found", ConsoleColoredPrinter.Color.YELLOW);
      String conflictDescription = String.format("\t%s", file);
      ConsoleColoredPrinter.println(conflictDescription, ConsoleColoredPrinter.Color.RED);
      ConsoleColoredPrinter.println("Press \"base\" (base) for current commit version, \"o\" (override) for replace, \"q\" (quit) for cancel");
      while (true) {
        char userInput = myScanner.next().charAt(0);
        switch (userInput) {
          case 'q':
            return MergeResolvingResult.STOP_MERGE;
          case 'b':
            return MergeResolvingResult.BASE_FILE;
          case 'o':
            return MergeResolvingResult.TARGET_FILE;
          default:
            ConsoleColoredPrinter.println("No such solution, try again");
        }
      }
    }
  }
}
