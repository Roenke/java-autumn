package com.spbau.bibaev.homework.torrent.client.api;

import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfo;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * @author Vitaliy.Bibaev
 */
public interface ClientStateEx extends ClientState {
  void addFilePart(@NotNull Path file, int part);

  void addNewFile(@NotNull Path file, @NotNull ClientFileInfo info);
}
