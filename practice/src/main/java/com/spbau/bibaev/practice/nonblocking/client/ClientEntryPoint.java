package com.spbau.bibaev.practice.nonblocking.client;

import com.spbau.bibaev.practice.nonblocking.server.ServerEntryPoint;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class ClientEntryPoint {
  public static void main(String[] args) throws IOException {
    Socket socket = new Socket("localhost", ServerEntryPoint.PORT);

    try (DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
      os.writeUTF(ServerEntryPoint.FILE_PATH);
      MessageDigest digest = DigestUtils.getSha1Digest();
      try (InputStream is = new DigestInputStream(socket.getInputStream(), digest)) {
        byte[] buffer = new byte[4096];
        int read = is.read(buffer);
        while (read > 0) {
          read = is.read(buffer);
        }
        System.out.println(DigestUtils.sha1Hex(digest.digest()));
      }
    }
  }
}
