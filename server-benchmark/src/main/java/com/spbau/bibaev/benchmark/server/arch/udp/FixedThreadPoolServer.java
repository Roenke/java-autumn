package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Vitaliy.Bibaev
 */
public class FixedThreadPoolServer implements Runnable {
  private static final int PORT = Details.UdpPorts.FIXED_THREAD_POOL;
  private final byte[] myBuffer = new byte[50 * 1024 * 1024]; // 10 mb

  public FixedThreadPoolServer() {
    super();
  }

  @Override
  public void run() {
    try {
      final DatagramSocket datagramSocket = new DatagramSocket(PORT);
      final DatagramPacket datagram = new DatagramPacket(myBuffer, myBuffer.length);
      datagramSocket.receive(datagram);

      final int[] array = DataUtils.read(datagram);
      InsertionSorter.sort(array);
      DataUtils.write(array, datagram, myBuffer);

    } catch (IOException e) {
      System.err.println("Something went wrong with server on port " + PORT);
    }

  }
}
