package com.spbau.bibaev.homework.torrent.server;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy.Bibaev
 */
public class ConnectedClientsRefresher implements Runnable {
  private final ServerStateEx myServerState;
  private final ScheduledExecutorService myExecutorService;

  ConnectedClientsRefresher(@NotNull ServerStateEx serverState, @NotNull ScheduledExecutorService executorService) {
    myServerState = serverState;
    myExecutorService = executorService;
  }

  @Override
  public void run() {
    final long now = System.currentTimeMillis();
    final Collection<ClientInfo> clients = myServerState.getConnectedClients();

    for (ClientInfo client : clients) {
      final Timestamp lastConnectionTime = myServerState.getLastConnectionTime(client);
      if (lastConnectionTime != null && now - lastConnectionTime.getTime() >= Details.Server.TIME_TO_RELEASE_FILES_MILLIS) {
        myServerState.forgetClient(client);
      }
    }

    final Long toNextCheck = myServerState.getConnectedClients().stream()
        .map(myServerState::getLastConnectionTime)
        .filter(Objects::nonNull)
        .max(Timestamp::compareTo)
        .map(x -> Details.Server.TIME_TO_RELEASE_FILES_MILLIS - (now - x.getTime()))
        .orElse(Details.Server.TIME_TO_RELEASE_FILES_MILLIS);

    myExecutorService.schedule(this, toNextCheck, TimeUnit.MILLISECONDS);
  }
}
