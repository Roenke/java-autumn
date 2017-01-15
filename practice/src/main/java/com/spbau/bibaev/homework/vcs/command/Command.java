package com.spbau.bibaev.homework.vcs.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Command {
  @NotNull
  CommandResult perform(@NotNull List<String> args);
}
