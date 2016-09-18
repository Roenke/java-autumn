package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.EntryPoint;
import com.spbau.bibaev.homework.vcs.util.ConsoleColoredPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public abstract class CommandBase implements Command {
  protected final File ourDirectory;

  public CommandBase(@NotNull File directory) {
    ourDirectory = directory;
  }

  @Override
  public void perform(@NotNull List<String> args) {
    int argCount = args.size();
    if (getMinArgCount() <= argCount && argCount <= getMaxArgCount()) {
      performImpl(args);
    } else {
      ConsoleColoredPrinter.println(String.format("Usage: %s %s", EntryPoint.VCS_NAME, getUsage()),
          ConsoleColoredPrinter.RED);
    }
  }

  protected abstract void performImpl(@NotNull List<String> args);
  protected abstract String getUsage();

  protected int getMinArgCount() {
    return 0;
  }

  protected int getMaxArgCount() {
    return Integer.MAX_VALUE;
  }
}
