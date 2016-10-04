package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class AddCommand extends RepositoryCommand {
  public AddCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    List<Path> paths = args.stream().map(ourDirectory::resolve).collect(Collectors.toList());
    boolean failed = false;
    for (Path path : paths) {
      if (!path.toFile().exists()) {
        failed = true;
        ConsoleColoredPrinter.println(String.format("File %s not found", path), ConsoleColoredPrinter.Color.RED);
      }

      Commit lastRevision = repository.getCurrentBranch().getCommit();
//      FileState fileState = lastRevision.getFileState(ourDirectory.relativize(path));
//      if (fileState != FileState.NEW && fileState != FileState.MODIFIED) {
//        failed = true;
//        ConsoleColoredPrinter.println(String.format("File %s not new or modifier, cannot add to index", path),
//            ConsoleColoredPrinter.Color.RED);
//      }
    }

    if(failed) {
      ConsoleColoredPrinter.println("Operation failed", ConsoleColoredPrinter.Color.RED);
      return CommandResult.FAILED;
    }

    // TODO: add all files to index

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "add {[file]}";
  }
}
