package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.Details;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Vitaliy.Bibaev
 */
public class NewThreadProcessingServer extends UdpServer {
  public NewThreadProcessingServer() {
    super(Details.UdpPorts.THREAD_PER_REQUEST);
  }

  @Override
  protected void start(@NotNull DatagramSocket socket, @NotNull DatagramPacket packet) throws IOException {
    while (!socket.isClosed()) {
      socket.receive(packet);

      new Thread(() -> {
        try {
          handle(socket, packet);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }
}
