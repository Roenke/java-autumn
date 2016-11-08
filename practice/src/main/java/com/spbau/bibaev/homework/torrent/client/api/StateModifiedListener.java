package com.spbau.bibaev.homework.torrent.client.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Vitaliy.Bibaev
 */
public interface StateModifiedListener {
  void stateModified(@NotNull ClientState state);
}
