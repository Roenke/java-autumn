package com.spbau.bibaev.benchmark.server.arch.udp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class UdpServer implements Runnable {
  private static final int RECEIVE_BUFFER_SIZE = 10 * 1024 * 1024; // 10 mb
  private static final int SEND_BUFFER_SIZE = 10 * 1024 * 1024; // 10 mb

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

  protected abstract void start(@NotNull DatagramSocket socket, @NotNull DatagramPacket datagram) throws IOException;
}
