package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.ServerState;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SourcesHandler extends ReadOnlyRequestHandler {
  @Override
  void handleReadOnlyRequest(@NotNull Socket clientSocket, @NotNull ServerState serverState) throws IOException {
    try (DataInputStream is = new DataInputStream(clientSocket.getInputStream())) {
      final int id = is.readInt();
      try(DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
        
      }
    }
  }
}
