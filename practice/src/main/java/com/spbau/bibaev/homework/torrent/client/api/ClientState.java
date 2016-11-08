package com.spbau.bibaev.homework.torrent.client.api;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * @author Vitaliy.Bibaev
 */
public interface ClientState {
  Collection<Path> getFiles();

  Map<Path, ClientFileInfo> getFile2Info();

  Collection<Integer> getIds();

  @Nullable
  Path getPathById(int id);

  @Nullable
  ClientFileInfo getInfoById(int id);
}
