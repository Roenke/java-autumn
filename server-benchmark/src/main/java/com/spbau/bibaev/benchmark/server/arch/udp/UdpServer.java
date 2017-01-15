package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.arch.ServerWithStatistics;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class UdpServer extends ServerWithStatistics {
  static final int RECEIVE_BUFFER_SIZE = 1024 * 1024 * 100; // 10 mb
  private static final int SEND_BUFFER_SIZE = 1024 * 1024 * 100; // 10 mb

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
      start(mySocket);
    } catch (SocketException ignored) {

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void shutdown() {
    if (mySocket != null) {
      mySocket.close();
    }
  }

  void handle(@NotNull DatagramSocket socket, @NotNull MessageProtos.Array message,
              @NotNull InetAddress address, int port) throws IOException {
    final long requestTime = System.nanoTime();
    final long clientTime = System.nanoTime();
    final int[] array = DataUtils.unbox(message);
    InsertionSorter.sort(array);
    final DatagramPacket resultPacket = DataUtils.createPacket(array);
    resultPacket.setAddress(address);
    resultPacket.setPort(port);
    final long requestDuration = System.nanoTime() - requestTime;
    socket.send(resultPacket);
    final long clientDuration = System.nanoTime() - clientTime;

    updateStatistics(clientDuration, requestDuration);
  }

  void handle(@NotNull DatagramSocket socket, @NotNull DatagramPacket packet) throws IOException {
    final long requestTime = System.nanoTime();
    final long clientTime = System.nanoTime();
    final int[] array = DataUtils.read(packet);
    InsertionSorter.sort(array);
    final DatagramPacket resultPacket = DataUtils.createPacket(array);
    resultPacket.setPort(packet.getPort());
    resultPacket.setAddress(packet.getAddress());
    final long requestDuration = System.nanoTime() - requestTime;
    socket.send(resultPacket);
    final long clientDuration = System.nanoTime() - clientTime;

    updateStatistics(clientDuration, requestDuration);
  }

  protected abstract void start(@NotNull DatagramSocket socket) throws IOException;
}
