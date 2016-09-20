package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.repository.Project;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.repository.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.Diff;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class CheckoutCommand extends RepositoryCommand {
  public CheckoutCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) {
    Revision currentRevision = repository.getCurrentBranch().getLastRevision();
    Project project = repository.getProject();
    Diff diff = project.diffWithRevision(currentRevision);
    Collection<Path> newFiles = diff.getNewFiles();
    Collection<Path> modifiedFiles = diff.getModifiedFiles();
    if (newFiles.size() + modifiedFiles.size() > 0) {
      ConsoleColoredPrinter.println("Repository contains uncommitted files. Commit/revert it.",
          ConsoleColoredPrinter.RED);
      ConsoleColoredPrinter.printListOfFiles("New", ConsoleColoredPrinter.RED, FilesUtil.pathsToStrings(newFiles));
      ConsoleColoredPrinter.printListOfFiles("Modified", ConsoleColoredPrinter.YELLOW,
          FilesUtil.pathsToStrings(modifiedFiles));
      return;
    }

    try {
      Path tmpDirectory = Files.createTempDirectory(currentRevision.getHash());
      currentRevision.restore(tmpDirectory);
      project.clean();
      FilesUtil.recursiveCopyDirectory(tmpDirectory, project.getRootDirectory().toPath());
    } catch (IOException | RepositoryIOException e) {
      ConsoleColoredPrinter.println("Error occurred:" + e.getMessage());
    }
  }

  @Override
  protected int getMinArgCount() {
    return 1;
  }

  @Override
  protected int getMaxArgCount() {
    return 1;
  }

  @Override
  protected String getUsage() {
    return "checkout branch_name|revision_hash";
  }
}
