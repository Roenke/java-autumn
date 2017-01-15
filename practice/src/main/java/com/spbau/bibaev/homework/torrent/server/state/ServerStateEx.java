package com.spbau.bibaev.homework.torrent.server.state;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * @author Vitaliy.Bibaev
 */
public interface ServerStateEx extends ServerState {
  int addNewFile(@NotNull FileInfo fileInfo);

  void updateSharedFiles(@NotNull ClientInfo client, @NotNull Set<Integer> ids);

  void updateClientConnectionTime(@NotNull ClientInfo client, @NotNull Timestamp time);

  @Nullable
  Timestamp getLastConnectionTime(@NotNull ClientInfo client);

  void forgetClient(@NotNull ClientInfo client);
}
