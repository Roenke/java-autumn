package com.spbau.bibaev.homework.torrent.client.handler;

import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.common.AbstractRequestHandler;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Vitaliy.Bibaev
 */
public class GetHandler extends AbstractRequestHandler<ClientState> {
  @Override
  protected void handleImpl(@NotNull Socket socket, @NotNull ClientState state) throws IOException {
    try (DataInputStream is = new DataInputStream(socket.getInputStream())) {
      final int id = is.readInt();
      final int partNumber = is.readInt();

      Path path = state.getPathById(id);
      final ClientFileInfo info = state.getInfoById(id);
      if (path == null || info == null) {
        // TODO handle this situation

      } else {
        if (!info.getParts().contains(partNumber)) {
          // TODO: send part not found error
        }
        final InputStream inputStream = Files.newInputStream(path);
        try (OutputStream out = socket.getOutputStream()) {
          IOUtils.copyLarge(inputStream, out, partNumber * Details.FILE_PART_SIZE, Details.FILE_PART_SIZE);
        }
      }

    }
  }
}
