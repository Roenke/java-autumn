package com.spbau.bibaev.homework.torrent.client.impl;

import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.server.FileInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerImpl implements Server {

  @Override
  public Map<Integer, FileInfo> list() {
    return null;
  }

  @Override
  public int upload(@NotNull FileInfo info) {
    return 0;
  }

  @Override
  public List<ClientInfo> sources(int fileId) {
    return null;
  }

  @Override
  public boolean update(int port, @NotNull List<Integer> ids) {
    return false;
  }
}
