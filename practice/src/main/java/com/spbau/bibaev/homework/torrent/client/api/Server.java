package com.spbau.bibaev.homework.torrent.client.api;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy.Bibaev
 */
public interface Server {
  Map<Integer, FileInfo> list() throws IOException;

  int upload(@NotNull FileInfo info) throws IOException;

  List<ClientInfo> sources(int fileId) throws IOException;

  boolean update(int port, @NotNull Collection<Integer> ids) throws IOException;
}
