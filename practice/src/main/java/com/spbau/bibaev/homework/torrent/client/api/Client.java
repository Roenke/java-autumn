package com.spbau.bibaev.homework.torrent.client.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public interface Client {
  List<Integer> stat(int id) throws IOException;

  boolean get(int id, int partNumber, @NotNull Path out) throws IOException;
}
