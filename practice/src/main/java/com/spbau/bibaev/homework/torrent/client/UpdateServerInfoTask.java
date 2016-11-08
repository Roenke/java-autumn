package com.spbau.bibaev.homework.torrent.client;

import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy.Bibaev
 */
public class UpdateServerInfoTask implements Runnable, ExitListener {
  private static final Logger LOG = LogManager.getLogger(UpdateServerInfoTask.class);

  private final ClientState myState;
  private final ScheduledExecutorService myExecutor;
  private final Server myServer;
  private final int myPort;

  public UpdateServerInfoTask(@NotNull ClientState state, @NotNull InetAddress serverAddress,
                              int serverPort, int clientPort) {
    myState = state;
    myPort = clientPort;
    myServer = new ServerImpl(serverAddress, serverPort);
    myExecutor = Executors.newScheduledThreadPool(1);
  }

  public void startAsync() {
    myExecutor.schedule(this, 0, TimeUnit.MILLISECONDS);
  }

  @Override
  public void run() {
    final boolean result;
    try {
      result = myServer.update(myPort, myState.getIds());
      if (result) {
        LOG.info("Files was successfully updated");
      }
    } catch (IOException e) {
      LOG.warn("Cannot sent update command to the server.", e);
    }

    myExecutor.schedule(this, Details.Client.UPDATE_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
  }

  @Override
  public void onExit() {
    LOG.info("exit command received");
    myExecutor.shutdownNow();
  }
}
