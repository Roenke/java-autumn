package com.spbau.bibaev.homework.torrent.client.api;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public interface Client {
  List<Integer> stat(int id) throws IOException;

  boolean get(int id, int partNumber, RandomAccessFile out) throws IOException;
}
