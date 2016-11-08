package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.ServerStateEx;
import com.spbau.bibaev.homework.torrent.server.TorrentServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public abstract class RequestHandler {
  private final Logger LOG = LogManager.getLogger(this.getClass());
  public void handle(@NotNull Socket clientSocket, @NotNull ServerStateEx serverState) {
    try (Socket socket = clientSocket) {
      handleImpl(socket, serverState);
    } catch (IOException e) {
      LOG.warn("Request handling failed");
    }
  }

  protected abstract void handleImpl(@NotNull Socket socket, @NotNull ServerStateEx serverState) throws IOException;
}
