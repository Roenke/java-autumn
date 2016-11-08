package com.spbau.bibaev.homework.torrent.server;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public interface ServerStateEx extends ServerState {
  int addNewFile(@NotNull FileInfo fileInfo);

  void updateSharedFiles(@NotNull ClientInfo client, @NotNull List<Integer> ids);

  void updateClientConnectionTime(@NotNull ClientInfo client, @NotNull Timestamp time);

  @Nullable
  Timestamp getLastConnectionTime(@NotNull ClientInfo client);

  void forgetClient(@NotNull ClientInfo client);
}
