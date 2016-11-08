package com.spbau.bibaev.homework.torrent.client.impl;

import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import com.spbau.bibaev.homework.torrent.common.Ip4ClientInfo;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerImpl implements Server {
  private final InetAddress myAddress;
  private final int myPort;

  public ServerImpl(@NotNull InetAddress address, int port) {
    myAddress = address;
    myPort = port;
  }

  @Override
  public Map<Integer, FileInfo> list() throws IOException {
    try (Socket socket = new Socket(myAddress, myPort);
         DataOutputStream out = new DataOutputStream(socket.getOutputStream());
         DataInputStream is = new DataInputStream(socket.getInputStream())) {
      out.writeByte(Details.Server.LIST_REQUEST_ID);

      final int count = is.readInt();
      final Map<Integer, FileInfo> files = new HashMap<>();
      for (int i = 0; i < count; i++) {
        final int id = is.readInt();
        final String name = is.readUTF();
        final long size = is.readLong();

        files.put(id, new FileInfo(name, size));
      }
      return files;
    }
  }

  @Override
  public int upload(@NotNull FileInfo info) throws IOException {
    try (Socket socket = new Socket(myAddress, myPort);
         DataInputStream is = new DataInputStream(socket.getInputStream());
         DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      out.writeByte(Details.Server.UPLOAD_REQUEST_ID);
      out.writeUTF(info.getName());
      out.writeLong(info.getSize());

      return is.readInt();
    }
  }

  @Override
  public List<ClientInfo> sources(int fileId) throws IOException {
    try (Socket socket = new Socket(myAddress, myPort);
         DataInputStream is = new DataInputStream(socket.getInputStream());
         DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      out.writeByte(Details.Server.SOURCES_REQUEST_ID);
      out.writeInt(fileId);

      final int count = is.readInt();

      final List<ClientInfo> result = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        final Ip4ClientInfo clientInfo = new Ip4ClientInfo(is.readByte(), is.readByte(),
            is.readByte(), is.readByte(), is.readShort());
        result.add(clientInfo);
      }

      return result;
    }
  }

  @Override
  public boolean update(int port, @NotNull Collection<Integer> ids) throws IOException {
    try (Socket socket = new Socket(myAddress, myPort);
         DataInputStream is = new DataInputStream(socket.getInputStream());
         DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      out.writeByte(Details.Server.UPDATE_REQUEST_ID);
      out.writeShort(port);
      Collection<Integer> copy = new ArrayList<>(ids);
      out.writeInt(copy.size());
      for (int id : copy) {
        out.writeInt(id);
      }

      return is.readBoolean();
    }
  }
}
