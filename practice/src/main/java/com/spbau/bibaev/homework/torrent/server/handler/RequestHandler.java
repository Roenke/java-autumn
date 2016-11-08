package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public abstract class RequestHandler {
  private final Logger LOG = LogManager.getLogger(this.getClass());
  public void handle(@NotNull Socket clientSocket, @NotNull ServerStateEx serverState) {
    try {
      handleImpl(clientSocket, serverState);
    } catch (IOException e) {
      LOG.error("Request handling failed", e);
    }
  }

  protected abstract void handleImpl(@NotNull Socket socket, @NotNull ServerStateEx serverState) throws IOException;
}
