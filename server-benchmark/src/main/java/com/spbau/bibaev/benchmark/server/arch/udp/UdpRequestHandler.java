package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Vitaliy.Bibaev
 */
class UdpRequestHandler implements Runnable {
  private final MessageProtos.Array myRequest;
  private final InetAddress myAnswerAddress;
  private final int myAnswerPort;
  private final DatagramSocket mySocket;

  UdpRequestHandler(@NotNull DatagramSocket socket, @NotNull MessageProtos.Array request, @NotNull InetAddress address, int port) {
    myRequest = request;
    myAnswerAddress = address;
    myAnswerPort = port;
    mySocket = socket;
  }

  @Override
  public void run() {
    final int[] array = DataUtils.unbox(myRequest);
    InsertionSorter.sort(array);
    try {
      final DatagramPacket packet = DataUtils.createPacket(array);
      packet.setPort(myAnswerPort);
      packet.setAddress(myAnswerAddress);
      mySocket.send(packet);
    } catch (IOException e) {
      // usual case - client disconnected
    }
  }
}
