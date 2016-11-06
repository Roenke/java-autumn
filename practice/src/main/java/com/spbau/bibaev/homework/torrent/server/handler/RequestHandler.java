package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public interface RequestHandler {
  void handle(@NotNull Socket clientSocket, @NotNull ServerStateEx serverState);
}
