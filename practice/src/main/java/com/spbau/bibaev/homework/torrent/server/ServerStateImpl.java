package com.spbau.bibaev.homework.torrent.server;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerStateImpl implements ServerStateEx {
  private final FileStorage myStorage;
  private final Map<ClientInfo, List<Integer>> myClient2Files = new ConcurrentHashMap<>();
  private final Map<ClientInfo, Timestamp> myClient2LastConnectionTime = new ConcurrentHashMap<>();

  public ServerStateImpl(@NotNull FileStorage storage) {
    myStorage = storage;
  }

  @Override
  public int addNewFile(@NotNull FileInfo fileInfo) {
    return myStorage.putNewFile(fileInfo);
  }

  @Override
  public void updateSharedFiles(@NotNull ClientInfo client, @NotNull List<Integer> ids) {
    myClient2Files.put(client, ids);
  }

  @Override
  public void updateClientConnectionTime(@NotNull ClientInfo client, @NotNull Timestamp time) {
    myClient2LastConnectionTime.put(client, time);
  }

  @Nullable
  @Override
  public Timestamp getLastConnectionTime(@NotNull ClientInfo client) {
    return myClient2LastConnectionTime.getOrDefault(client, null);
  }

  @Override
  public void forgetClient(@NotNull ClientInfo client) {
    myClient2LastConnectionTime.remove(client);
    myClient2Files.remove(client);
  }

  @Override
  public Map<Integer, FileInfo> getFiles() {
    return myStorage.getFiles();
  }

  @Override
  public Collection<Integer> getFilesByClient(@NotNull ClientInfo client) {
    return myClient2Files.get(client);
  }

  @Override
  public Collection<ClientInfo> getConnectedClients() {
    return Collections.unmodifiableCollection(myClient2Files.keySet());
  }
}
