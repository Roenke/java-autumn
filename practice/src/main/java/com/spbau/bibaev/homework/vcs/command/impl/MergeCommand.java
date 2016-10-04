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

//    Branch dstBranch = repository.getCurrentBranch();
//    MergeStrategy mergeStrategy = new MyPrimitiveMergeStrategy();
//    mergeStrategy.merge(srcBranch.getLastRevision(), dstBranch.getLastRevision());
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
}