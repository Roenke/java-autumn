package com.spbau.bibaev.homework.torrent.client.api;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public interface Client {
  List<Integer> stat(int id);

  boolean get(int id, int partNumber, @NotNull OutputStream out);
}
