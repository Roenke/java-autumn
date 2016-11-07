package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public abstract class RequestHandler {
  public void handle(@NotNull Socket clientSocket, @NotNull ServerStateEx serverState) {
    try (Socket socket = clientSocket) {
      handleImpl(socket, serverState);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected abstract void handleImpl(@NotNull Socket socket, @NotNull ServerStateEx serverState) throws IOException;
}
