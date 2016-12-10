package com.spbau.bibaev.benchmark.server.arch.udp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.arch.ServerWithStatistics;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class UdpServer extends ServerWithStatistics {
  private static final int RECEIVE_BUFFER_SIZE = 1 << 16; // 64 kb
  private static final int SEND_BUFFER_SIZE = 1 << 16; // 64 kb

  private final int myPort;

  private volatile DatagramSocket mySocket;

  UdpServer(int port) {
    myPort = port;
  }

  @Override
  public void run() {
    try {
      mySocket = new DatagramSocket(myPort);
      mySocket.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
      mySocket.setSendBufferSize(SEND_BUFFER_SIZE);
    } catch (SocketException e) {
      System.err.println("Cannot start udp server");
      e.printStackTrace();
      return;
    }

    try {
      byte[] buffer = new byte[RECEIVE_BUFFER_SIZE];
      final DatagramPacket datagram = new DatagramPacket(buffer, RECEIVE_BUFFER_SIZE);

      start(mySocket, datagram);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    if (mySocket != null) {
      mySocket.close();
    }
  }

  void handle(@NotNull DatagramSocket socket, @NotNull DatagramPacket packet) throws IOException {
    final long requestTime = System.nanoTime();
    final long clientTime = System.nanoTime();
    final MessageProtos.Array request = DataUtils.readToArray(packet);
    final int[] array = DataUtils.unbox(request);
    InsertionSorter.sort(array);
    final DatagramPacket resultPacket = DataUtils.createPacket(array);
    resultPacket.setPort(packet.getPort());
    resultPacket.setAddress(packet.getAddress());
    final long requestDuration = System.nanoTime() - requestTime;
    socket.send(resultPacket);
    final long clientDuration = System.nanoTime() - clientTime;

    updateStatistics(clientDuration, requestDuration);
  }

  protected abstract void start(@NotNull DatagramSocket socket, @NotNull DatagramPacket datagram) throws IOException;
}
