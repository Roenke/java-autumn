package com.spbau.bibaev.homework.torrent.client.api;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * @author Vitaliy.Bibaev
 */
public interface ClientStateEx extends ClientState {
  boolean addFilePart(@NotNull Path file, int part);

  boolean addNewFile(@NotNull Path file, @NotNull ClientFileInfo info);
}
