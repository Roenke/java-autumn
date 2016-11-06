package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.ServerState;
import com.spbau.bibaev.homework.torrent.server.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class ReadOnlyRequestHandler implements RequestHandler {
  @Override
  public void handle(@NotNull Socket clientSocket, @NotNull ServerStateEx serverState) {
    handleImpl(clientSocket, serverState);
  }

  abstract void handleImpl(@NotNull Socket clientSocket, @NotNull ServerState serverState);
}
