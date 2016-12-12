package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.Details;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Vitaliy.Bibaev
 */
public class NewThreadProcessingServer extends UdpServer {
  public NewThreadProcessingServer() {
    super(Details.UdpPorts.THREAD_PER_REQUEST);
  }

  @Override
  protected void start(@NotNull DatagramSocket socket) throws IOException {
    while (!socket.isClosed()) {
      byte[] buffer = new byte[RECEIVE_BUFFER_SIZE];
      final DatagramPacket datagram = new DatagramPacket(buffer, RECEIVE_BUFFER_SIZE);
      socket.receive(datagram);

      new Thread(() -> {
        try {
          handle(socket, datagram);
        } catch (SocketException ignored) {

        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }
}
