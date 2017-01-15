package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    byte[] buffer = new byte[RECEIVE_BUFFER_SIZE];
    final DatagramPacket datagram = new DatagramPacket(buffer, RECEIVE_BUFFER_SIZE);
    while (!socket.isClosed()) {
      socket.receive(datagram);

      final MessageProtos.Array array = DataUtils.readToArray(datagram);
      final InetAddress address = datagram.getAddress();
      final int port = datagram.getPort();
      new Thread(() -> {
        try {
          handle(socket, array, address, port);
        } catch (SocketException ignored) {
          ignored.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }
}
