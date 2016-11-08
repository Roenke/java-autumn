package com.spbau.bibaev.homework.torrent.client.handler;

import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.common.AbstractRequestHandler;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

/**
 * @author Vitaliy.Bibaev
 */
public class StatHandler extends AbstractRequestHandler<ClientState> {
  @Override
  protected void handleImpl(@NotNull Socket socket, @NotNull ClientState state) throws IOException {
    try (DataInputStream is = new DataInputStream(socket.getInputStream())) {
      final int id = is.readInt();
      final ClientFileInfo info = state.getInfoById(id);
      if (info == null) {
        // TODO: file not found error
      } else{
        final Set<Integer> parts = info.getParts();
        try(DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
          out.writeInt(parts.size());
          for(Integer part : parts) {
            out.writeInt(part);
          }
        }
      }
    }
  }
}
