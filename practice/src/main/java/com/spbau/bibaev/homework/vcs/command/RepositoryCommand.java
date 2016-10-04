package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class RepositoryCommand extends CommandBase {
  public RepositoryCommand(@NotNull Path directory) {
    super(directory);
  }

  /**
   * Implement with precondition that repository initialized
   *
   * @param args command line args
   */
  @NotNull
  @Override
  protected CommandResult performImpl(@NotNull List<String> args) throws IOException {
    try {
      Repository rep = RepositoryImpl.openRepository(ourDirectory);
      if (rep == null) {
        ConsoleColoredPrinter.println("Repository not found", ConsoleColoredPrinter.Color.RED);
      } else {
        final CommandResult result = perform(args, rep);
        if(result != CommandResult.FAILED) {
          rep.save();
        }

        return result;
      }
    } catch (IOException e) {
      ConsoleColoredPrinter.println("Error occurred: " + e, ConsoleColoredPrinter.Color.RED);
    }

    return CommandResult.FAILED;
  }

  protected abstract CommandResult perform(@NotNull List<String> args, @NotNull Repository repository) throws IOException;
}
