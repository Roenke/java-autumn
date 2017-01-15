package com.spbau.bibaev.homework.torrent.client.handler;

import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.api.ClientState;
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
    try (DataInputStream is = new DataInputStream(socket.getInputStream());
         OutputStream out = socket.getOutputStream()) {
      final int id = is.readInt();
      final int partNumber = is.readInt();

      LOG.info("part number " + partNumber + " requested. File id = " + id);
      final Path path = state.getPathById(id);
      final ClientFileInfo info = state.getInfoById(id);
      if (path == null || info == null) {
        LOG.error(String.format("File with id = %d not found", id));
        return;
      }

      if (!info.getParts().contains(partNumber)) {
        LOG.error(String.format("File %d not contains part %d", id, partNumber));
        return;
      }

      try (final InputStream inputStream = Files.newInputStream(path)) {
        IOUtils.copyLarge(inputStream, out, partNumber * Details.FILE_PART_SIZE, Details.FILE_PART_SIZE);
      }
    }
  }
}
