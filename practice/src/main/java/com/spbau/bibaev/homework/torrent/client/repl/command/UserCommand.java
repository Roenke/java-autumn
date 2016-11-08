package com.spbau.bibaev.homework.torrent.client.repl.command;

import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vitaliy.Bibaev
 */
public interface UserCommand {
  void execute(@NotNull ClientStateEx state, @NotNull String[] args);

  String getUsage();

  String getDescription();
}
