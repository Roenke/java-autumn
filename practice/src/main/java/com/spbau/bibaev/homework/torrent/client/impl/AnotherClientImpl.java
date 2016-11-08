package com.spbau.bibaev.homework.torrent.client.impl;

import com.spbau.bibaev.homework.torrent.client.api.Client;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class AnotherClientImpl implements Client {
  private final InetAddress myAddress;
  private final int myPort;

  public AnotherClientImpl(@NotNull InetAddress address, int port) {
    myAddress = address;
    myPort = port;
  }

  @Override
  public List<Integer> stat(int id) throws IOException {
    try (Socket socket = new Socket(myAddress, myPort);
         DataInputStream is = new DataInputStream(socket.getInputStream());
         DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      out.writeByte(Details.Client.STAT_REQUEST_ID);
      out.writeInt(id);

      int count = is.readInt();
      final List<Integer> result = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        int part = is.readInt();
        result.add(part);
      }

      return result;
    }
  }

  @Override
  public boolean get(int id, int partNumber, @NotNull Path outPath) throws IOException {
    try (Socket socket = new Socket(myAddress, myPort);
         DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
         InputStream is = socket.getInputStream()) {

      outputStream.writeByte(Details.Client.GET_REQUEST_ID);
      outputStream.writeInt(id);
      outputStream.writeInt(partNumber);

      try (RandomAccessFile out = new RandomAccessFile(outPath.toFile(), "rw")) {
        out.seek(Details.FILE_PART_SIZE * partNumber);
        IOUtils.copyLarge(is, new OutputStream() {
          @Override
          public void write(int b) throws IOException {
            out.write(b);
          }
        });
      }


      return true;
    }
  }
}
