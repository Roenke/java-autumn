package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.server.FileInfo;
import com.spbau.bibaev.homework.torrent.server.ServerState;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ListHandler extends ReadOnlyRequestHandler {
  @Override
  void handleReadOnlyRequest(@NotNull Socket clientSocket, @NotNull ServerState serverState) throws IOException {
    final Map<Integer, FileInfo> files = serverState.getFiles();
    try(DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
      out.write(files.size());
      for (int id : files.keySet()) {
        FileInfo info = files.get(id);
        out.writeInt(id);
        out.writeUTF(info.getName());
        out.writeLong(info.getSize());
      }
    }
  }
}
