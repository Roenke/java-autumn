package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.Ip4ClientInfo;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class UpdateHandler extends RequestHandler {
  @Override
  protected void handleImpl(@NotNull Socket socket, @NotNull ServerStateEx serverState) throws IOException {
    try (DataInputStream is = new DataInputStream(socket.getInputStream())) {
      int port = is.readShort();
      byte[] address = socket.getInetAddress().getAddress();
      ClientInfo client = new Ip4ClientInfo(address[0], address[1], address[2], address[3], port);

      int filesCount = is.readInt();
      List<Integer> ids = new ArrayList<>();
      for (int i = 0; i < filesCount; i++) {
        int fileId = is.readInt();
        ids.add(fileId);
      }

      serverState.updateSharedFiles(client, ids);
      serverState.updateClientConnectionTime(client, new Timestamp(System.currentTimeMillis()));

      try (DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
        os.writeBoolean(true);
      }
    }
  }
}
