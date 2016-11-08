package com.spbau.bibaev.homework.torrent.server;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import com.spbau.bibaev.homework.torrent.common.Ip4ClientInfo;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Vitaliy.Bibaev
 */
public class ConnectedClientsRefresherTest {
  private ServerStateEx state;

  @Before
  public void before() {
    Map<Integer, FileInfo> map = new HashMap<>();
    map.put(1, new FileInfo("first", 10));
    map.put(2, new FileInfo("second", 20));
    map.put(3, new FileInfo("third", 30));
    map.put(4, new FileInfo("four", 40));
    state = new ServerStateImpl(new FileStorage(map));
  }

  @Test
  public void setNextExecution() {
    ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

    ConnectedClientsRefresher refresher = new ConnectedClientsRefresher(state, executorService);
    refresher.run();

    verify(executorService).schedule(eq(refresher), anyLong(), any());
  }

  @Test
  public void correctReleaseClient() {
    ClientInfo client1 = new Ip4ClientInfo(1, 1, 1, 1, 1000);
    ClientInfo client2 = new Ip4ClientInfo(1, 1, 1, 2, 1000);
    state.updateSharedFiles(client1, Arrays.asList(1, 2));
    state.updateSharedFiles(client2, Arrays.asList(1, 4));
    state.updateClientConnectionTime(client1,
        new Timestamp(System.currentTimeMillis() - Details.Server.TIME_TO_RELEASE_FILES_MILLIS / 2));
    state.updateClientConnectionTime(client2,
        new Timestamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));

    ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

    new ConnectedClientsRefresher(state, executorService).run();

    assertEquals(1, state.getConnectedClients().size());
    assertEquals(client1, state.getConnectedClients().iterator().next());
  }

  @Test
  public void setCorrectDelay() {
    ClientInfo client1 = new Ip4ClientInfo(1, 1, 1, 1, 1000);
    state.updateSharedFiles(client1, Arrays.asList(1, 2));
    state.updateClientConnectionTime(client1,
        new Timestamp(System.currentTimeMillis() - Details.Server.TIME_TO_RELEASE_FILES_MILLIS / 2));
    ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

    final ConnectedClientsRefresher refresher = new ConnectedClientsRefresher(state, executorService);
    refresher.run();

    verify(executorService).schedule(eq(refresher),
        longThat(delay -> delay < Details.Server.TIME_TO_RELEASE_FILES_MILLIS), any());
  }
}
