package com.spbau.bibaev.homework.torrent.client.impl;

import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerImpl implements Server {
  private final InetAddress myAddress;
  private final int myPort;

  public ServerImpl(@NotNull InetAddress address, int port) {
    myAddress = address;
    myPort = port;
  }

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
  public boolean update(int port, @NotNull Collection<Integer> ids) {
    return false;
  }
}
