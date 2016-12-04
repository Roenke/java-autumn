package com.spbau.bibaev.homework.torrent.server.handler;

import com.spbau.bibaev.homework.torrent.common.FileInfo;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class UploadHandler extends RequestHandler {
  @Override
  protected void handleImpl(@NotNull Socket socket, @NotNull ServerStateEx serverState) throws IOException {
    try (DataInputStream is = new DataInputStream(socket.getInputStream())) {
      final String name = is.readUTF();
      final long size = is.readLong();
      final FileInfo info = new FileInfo(name, size);
      final int newFileId = serverState.addNewFile(info);
      try (DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
        os.writeInt(newFileId);
      }
    }
  }
}
