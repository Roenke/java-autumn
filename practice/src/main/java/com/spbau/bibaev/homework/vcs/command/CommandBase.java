package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.EntryPoint;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.RED;

public abstract class CommandBase implements Command {
  protected final Path ourDirectory;

  public CommandBase(@NotNull Path directory) {
    ourDirectory = directory;
  }

  @NotNull
  @Override
  public CommandResult perform(@NotNull List<String> args) {
    int argCount = args.size();
    if (getMinArgCount() <= argCount && argCount <= getMaxArgCount()) {
      try {
        return performImpl(args);
      } catch (IOException e) {
        ConsoleColoredPrinter.println("Something wrong. " + e, RED);
      }
    } else {
      ConsoleColoredPrinter.println(String.format("Usage: %s %s", EntryPoint.VCS_NAME, getUsage()), RED);
    }

    return CommandResult.FAILED;
  }

  @NotNull
  protected abstract CommandResult performImpl(@NotNull List<String> args) throws IOException;
  protected abstract String getUsage();

  protected int getMinArgCount() {
    return 0;
  }

  protected int getMaxArgCount() {
    return Integer.MAX_VALUE;
  }
}
