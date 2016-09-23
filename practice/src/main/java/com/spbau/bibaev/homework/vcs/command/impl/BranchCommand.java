package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class BranchCommand extends RepositoryCommand {
  public BranchCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull RepositoryImpl repository) throws RepositoryException {
    if (args.size() == 0) {
      String currentBranchName = repository.getCurrentBranchName();
      List<String> branches = repository.getAllBranches().stream().map(Branch::getName).collect(Collectors.toList());
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
      } else {
        repository.createNewBranch(branchName);
        ConsoleColoredPrinter.println("Successfully", ConsoleColoredPrinter.Color.GREEN);
      }
    }
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
