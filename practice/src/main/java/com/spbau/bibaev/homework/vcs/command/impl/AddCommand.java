package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.command.CommandResult;
import com.spbau.bibaev.homework.vcs.command.RepositoryCommand;
import com.spbau.bibaev.homework.vcs.repository.api.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.FilePersistentState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddCommand extends RepositoryCommand {
  public AddCommand(@NotNull Path directory) {
    super(directory);
  }

  @Override
  protected CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException {
    List<Path> paths = args.stream().map(ourDirectory::resolve).collect(Collectors.toList());
    boolean failed = false;

    Commit currentCommit = repository.getCurrentBranch().getCommit();
    final List<FilePersistentState> files = currentCommit.getRepositoryState().getFiles();
    Map<String, String> relativePath2Hash = new HashMap<>();
    for (FilePersistentState fileState : files) {
      relativePath2Hash.put(fileState.getRelativePath(), fileState.getMyHash());
    }

    Collection<String> added = repository.getIndex().added();
    for (Path path : paths) {
      if (!path.toFile().exists()) {
        failed = true;
        ConsoleColoredPrinter.println(String.format("File %s not found", path), ConsoleColoredPrinter.Color.RED);
        continue;
      }

      String relativePath = ourDirectory.relativize(path).toString();
      if (added.contains(relativePath)) {
        failed = true;
        ConsoleColoredPrinter.println(String.format("File %s already added", path), ConsoleColoredPrinter.Color.RED);
        continue;
      }

      String hash = FilesUtil.evalHashOfFile(path.toFile());

      FileState state;
      if (!relativePath2Hash.containsKey(relativePath)) {
        state = FileState.NEW;
      } else {
        final String repoHash = relativePath2Hash.get(relativePath);
        state = repoHash.equals(hash) ? FileState.NOT_CHANGED : FileState.MODIFIED;
      }

      if (state != FileState.NEW && state != FileState.MODIFIED) {
        failed = true;
        ConsoleColoredPrinter.println(String.format("File %s not new or modifier, cannot add to index", path),
            ConsoleColoredPrinter.Color.RED);
      }
    }

    if (failed) {
      ConsoleColoredPrinter.println("Operation failed", ConsoleColoredPrinter.Color.RED);
      return CommandResult.FAILED;
    }

    paths.forEach(repository::addFileToIndex);

    return CommandResult.SUCCESSFUL;
  }

  @Override
  protected String getUsage() {
    return "add {[file]}";
  }
}
