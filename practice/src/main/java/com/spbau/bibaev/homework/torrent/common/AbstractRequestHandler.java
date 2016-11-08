package com.spbau.bibaev.homework.torrent.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class AbstractRequestHandler<T> {
  protected final Logger LOG = LogManager.getLogger(this.getClass());

  public void handle(@NotNull Socket clientSocket, @NotNull T state) {
    try {
      handleImpl(clientSocket, state);
    } catch (IOException e) {
      LOG.warn("Request handling failed", e);
    }
  }

  protected abstract void handleImpl(@NotNull Socket socket, @NotNull T state) throws IOException;
}
