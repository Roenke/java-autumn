package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.v2.FilePersistentState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResetCommand extends RepositoryCommand {
  public ResetCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected String getUsage() {
    return "reset file";
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    Path file = ourDirectory.resolve(args.get(0));
    final Map<String, FilePersistentState> files = repository.getCurrentBranch().getCommit()
        .getRepositoryState().getFiles().stream()
        .collect(Collectors.toMap(FilePersistentState::getRelativePath, Function.identity()));
    final String relativePath = repository.getWorkingDirectory().getRootPath().relativize(file).toString();
    if (!files.containsKey(relativePath)) {
      ConsoleColoredPrinter.println("File not found in snapshot");
      return CommandResult.FAILED;
    }

    if(file.toFile().exists()) {
      FileUtils.deleteQuietly(file.toFile());
    }

    files.get(relativePath).restore(repository.getWorkingDirectory().getRootPath());
    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected int getMinArgCount() {
    return 1;
  }
}
