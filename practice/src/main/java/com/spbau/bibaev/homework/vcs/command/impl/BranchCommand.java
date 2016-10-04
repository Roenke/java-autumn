package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class BranchCommand extends RepositoryCommand {
  @SuppressWarnings("WeakerAccess")
  public BranchCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    if (args.size() == 0) {
      String currentBranchName = repository.getCurrentBranch().getName();
      List<String> branches = repository.getBranches().stream().map(Branch::getName).collect(Collectors.toList());
      branches.sort(String::compareTo);
      for (String branchName : branches) {
        if (currentBranchName.equals(branchName)) {
          ConsoleColoredPrinter.println(String.format("* %s", branchName), ConsoleColoredPrinter.Color.GREEN);
        } else {
          ConsoleColoredPrinter.println(String.format("  %s", branchName), ConsoleColoredPrinter.Color.WHITE);
        }
      }
    } else {
      String branchName = args.get(0);
      Branch branch = repository.getBranchByName(args.get(0));
      if (branch != null) {
        ConsoleColoredPrinter.println("Such branch already exists", ConsoleColoredPrinter.Color.RED);
        return CommandResult.FAILED;
      }
      repository.createNewBranch(branchName, repository.getCurrentBranch().getCommit());
      ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.Color.GREEN);
    }

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "branch [branch_name]";
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }
}
