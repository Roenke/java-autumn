package com.spbau.bibaev.homework.torrent.server.state;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * @author Vitaliy.Bibaev
 */
public interface ServerState {
  Map<Integer, FileInfo> getFiles();

  Collection<Integer> getFilesByClient(@NotNull ClientInfo client);

  Collection<ClientInfo> getConnectedClients();
}
