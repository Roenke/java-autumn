package com.spbau.bibaev.homework.vcs.command.impl;

import com.spbau.bibaev.homework.vcs.Repository;
import com.spbau.bibaev.homework.vcs.command.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogCommand extends CommandBase {
  public LogCommand(@NotNull Repository repository) {
    super(repository);
  }

  @Override
  public void perform(@NotNull List<String> args) {

  }
}
