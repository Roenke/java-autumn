package com.spbau.bibaev.homework.vcs.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Command {
  void perform(@NotNull List<String> args);
}
