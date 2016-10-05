package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.FilePersistentState;
import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RmCommand extends RepositoryCommand {
  public RmCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    List<Path> paths = args.stream().map(ourDirectory::resolve).collect(Collectors.toList());
    final Map<String, FilePersistentState> file2State = repository.getCurrentBranch().getCommit().getRepositoryState()
        .getFiles().stream().collect(Collectors.toMap(FilePersistentState::getRelativePath, Function.identity()));
    boolean failed = false;
    for (Path path : paths) {
      String relativePath = repository.getWorkingDirectory().getRootPath().relativize(path).toString();
      if (!path.toFile().exists() && !file2State.containsKey(relativePath)) {
        failed = true;
        ConsoleColoredPrinter.println(String.format("file \"%s\" not exists and not fount in current repo state",
                path.toString()), ConsoleColoredPrinter.Color.RED);
      }
    }

    if (failed) {
      ConsoleColoredPrinter.println("Operation failed", ConsoleColoredPrinter.Color.RED);
      return CommandResult.FAILED;
    }

    for(Path path : paths) {
      repository.removeFileFromIndex(path);
      FileUtils.deleteQuietly(path.toFile());
    }

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "rm {file}+";
  }

  @Override
  protected int getMinArgCount() {
    return 1;
  }
}
