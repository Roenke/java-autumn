package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryException;
import com.spbau.bibaev.homework.vcs.repository.impl.BranchImpl;
import com.spbau.bibaev.homework.vcs.repository.impl.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.repository.impl.RevisionImpl;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class MergeCommand extends RepositoryCommand {

  public MergeCommand(@NotNull File directory) {
    super(directory);
  }

  interface MergeStrategy {
    @NotNull
    RevisionImpl merge(@NotNull RevisionImpl from, @NotNull RevisionImpl into) throws RepositoryException;
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull RepositoryImpl repository) throws RepositoryException {
    BranchImpl srcBranch = repository.getBranchByName(args.get(0));

    if(srcBranch == null) {
      ConsoleColoredPrinter.println("Such branch not found", ConsoleColoredPrinter.Color.RED);
      return;
    }

    BranchImpl dstBranch = repository.getCurrentBranch();
    MergeStrategy mergeStrategy = new MyPrimitiveMergeStrategy();
    mergeStrategy.merge(srcBranch.getLastRevision(), dstBranch.getLastRevision());
    ConsoleColoredPrinter.println("Successfully");
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

  private static class MyPrimitiveMergeStrategy implements MergeStrategy {
    @Override
    @NotNull
    public RevisionImpl merge(@NotNull RevisionImpl from, @NotNull RevisionImpl into) {
      return null;
    }
  }
}
