package com.spbau.bibaev.homework.torrent.server;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Vitaliy.Bibaev
 */
public class Ip4ClientInfo implements ClientInfo {
  private final byte[] myAddress;
  private final int myPort;

  public Ip4ClientInfo(byte b1, byte b2, byte b3, byte b4, int port) {
    myAddress = new byte[]{b1, b2, b3, b4};
    myPort = port;
  }

  public Ip4ClientInfo(int b1, int b2, int b3, int b4, int port) {
    this((byte) b1, (byte) b2, (byte) b3, (byte) b4, port);
  }

  @Override
  public int hashCode() {
    return Objects.hash(myPort, Arrays.hashCode(myAddress));
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Ip4ClientInfo)) {
      return false;
    }

    final Ip4ClientInfo other = (Ip4ClientInfo) obj;
    return myPort == other.getPort() && Arrays.equals(myAddress, other.myAddress);
  }

  @Override
  public byte[] getIp() {
    return Arrays.copyOf(myAddress, 4);
  }

  @Override
  public int getPort() {
    return myPort;
  }
}
