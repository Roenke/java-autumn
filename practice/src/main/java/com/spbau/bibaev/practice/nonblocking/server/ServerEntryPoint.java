package com.spbau.bibaev.practice.nonblocking.server;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

public class ServerEntryPoint {
  public static final int PORT = 23423;
  public static final String FILE_PATH = ".gitignore";
  private byte[] cache = null;

  private ServerEntryPoint() {
  }

  public static void main(String[] args) throws IOException {
    new ServerEntryPoint().run();
  }

  private void run() throws IOException {
    ServerSocket socket = new ServerSocket(PORT);
    while (true) {
      Socket clientSocket = socket.accept();
      SocketChannel channel = clientSocket.getChannel();
      channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
      try (DataInputStream is = new DataInputStream(clientSocket.getInputStream())) {
        String relativePath = is.readUTF();
        Path path = Paths.get(relativePath);
        if (!path.toFile().exists()) {
          System.err.println("File " + path.toAbsolutePath() + " not found");
        } else {
          MessageDigest digest = DigestUtils.getSha1Digest();
          Files.copy(path, new DigestOutputStream(clientSocket.getOutputStream(), digest));
          System.out.println(DigestUtils.sha1Hex(digest.digest()));
        }
      }
    }
  }
}
