package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.server.state.ServerState;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Vitaliy.Bibaev
 */
public class SourcesHandler extends ReadOnlyRequestHandler {
  @Override
  void handleReadOnlyRequest(@NotNull Socket clientSocket, @NotNull ServerState serverState) throws IOException {
    try (DataInputStream is = new DataInputStream(clientSocket.getInputStream())) {
      final int targetFileId = is.readInt();
      try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
        final Collection<ClientInfo> clients = serverState.getConnectedClients();

        Collection<ClientInfo> clientsWithTargetFile = clients.stream()
            .filter(client -> serverState.getFilesByClient(client).contains(targetFileId))
            .collect(Collectors.toList());

        out.writeInt(clientsWithTargetFile.size());
        for (ClientInfo client : clientsWithTargetFile) {
          for (byte b : client.getIp()) {
            out.writeByte(b);
          }

          out.writeShort(client.getPort());
        }
      }
    }
  }
}
