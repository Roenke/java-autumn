package com.spbau.bibaev.homework.torrent.client.api;

import java.util.Set;

/**
 * @author Vitaliy.Bibaev
 */
public interface ClientFileInfo {
  int getId();

  long getSize();

  boolean isLoaded();

  Set<Integer> getParts();
}
