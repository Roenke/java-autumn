package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.ServerState;
import com.spbau.bibaev.homework.torrent.server.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class ReadOnlyRequestHandler extends RequestHandler {
  @Override
  protected void handleImpl(@NotNull Socket socket, @NotNull ServerStateEx serverState) throws IOException {
    handleReadOnlyRequest(socket, serverState);
  }

  abstract void handleReadOnlyRequest(@NotNull Socket clientSocket, @NotNull ServerState serverState)
      throws IOException;
}
