package com.spbau.bibaev.homework.torrent.server.state;

import org.jetbrains.annotations.NotNull;

/**
 * @author Vitaliy.Bibaev
 */
public interface FilesChangedListener {
  void stateChanged(@NotNull SharedFiles newState);
}
