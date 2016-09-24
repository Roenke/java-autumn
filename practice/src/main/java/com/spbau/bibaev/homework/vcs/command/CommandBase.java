package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.EntryPoint;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter.Color.RED;

public abstract class CommandBase implements Command {
  protected final File ourDirectory;

  public CommandBase(@NotNull File directory) {
    ourDirectory = directory;
  }

  @Override
  public void perform(@NotNull List<String> args) {
    int argCount = args.size();
    if (getMinArgCount() <= argCount && argCount <= getMaxArgCount()) {
      try {
        performImpl(args);
      } catch (IOException e) {
        ConsoleColoredPrinter.println("Something wrong. " + e.getMessage());
      }
    } else {
      ConsoleColoredPrinter.println(String.format("Usage: %s %s", EntryPoint.VCS_NAME, getUsage()), RED);
    }
  }

  protected abstract void performImpl(@NotNull List<String> args) throws IOException;
  protected abstract String getUsage();

  protected int getMinArgCount() {
    return 0;
  }

  protected int getMaxArgCount() {
    return Integer.MAX_VALUE;
  }
}
