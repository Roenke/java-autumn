package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.Branch;
import com.spbau.bibaev.homework.vcs.repository.Project;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.repository.Revision;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.FileState;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.printListOfFiles;

public class StatusCommand extends RepositoryCommand {
  public StatusCommand(@NotNull File directory) {
    super(directory);
  }

  @Override
  protected void perform(@NotNull List<String> args, @NotNull Repository repository) {
    Branch currentBranch = repository.getCurrentBranch();
    Revision lastRevision = currentBranch.getLastRevision();

    ConsoleColoredPrinter.println("On branch " + currentBranch.getName());
    ConsoleColoredPrinter.println("Revision: " + lastRevision.getHash(), ConsoleColoredPrinter.GREEN);

    Project project = repository.getProject();
    Path projectRoot = project.getRootDirectory().toPath();
    List<File> projectFiles = project.getAllFiles();
    Collection<String> newFiles = new ArrayList<>();
    Collection<String> modifiedFiles = new ArrayList<>();
    try {
      for (File file : projectFiles) {
        String relativePath = projectRoot.relativize(file.toPath()).toString();
        String hash = FilesUtil.evalHashOfFile(file);
        FileState state = lastRevision.getFileState(relativePath, hash);
        switch (state) {
          case NEW:
            newFiles.add(relativePath);
            break;
          case MODIFIED:
            modifiedFiles.add(relativePath);
            break;
        }
      }
    } catch (IOException e) {
      ConsoleColoredPrinter.println("Error occurred: " + e.getMessage());
    }
    printListOfFiles("New files", ConsoleColoredPrinter.GREEN, newFiles);
    printListOfFiles("Modified", ConsoleColoredPrinter.YELLOW, modifiedFiles);

    Set<String> fileNames = projectFiles.stream()
        .map(file -> projectRoot.relativize(file.toPath()).toString()).collect(Collectors.toSet());
    Set<String> deletedFiles = lastRevision.getAllFiles().stream().collect(Collectors.toSet());
    deletedFiles.removeAll(fileNames);
    printListOfFiles("Deleted", ConsoleColoredPrinter.RED, deletedFiles);
  }

  @Override
  protected String getUsage() {
    return "status";
  }

  @Override
  protected int getMaxArgCount() {
    return 0;
  }
}
