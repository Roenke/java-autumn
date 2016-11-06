package com.spbau.bibaev.homework.torrent.server;

import java.util.Arrays;

/**
 * @author Vitaliy.Bibaev
 */
public class Ip4ClientInfo implements ClientInfo {
  private final byte[] myAddress;

  public Ip4ClientInfo(byte b1, byte b2, byte b3, byte b4) {
    myAddress = new byte[]{b1, b2, b3, b4};
  }

  public Ip4ClientInfo(int b1, int b2, int b3, int b4) {
    this((byte) b1, (byte) b2, (byte) b3, (byte) b4);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(myAddress);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Ip4ClientInfo && Arrays.equals(myAddress, ((Ip4ClientInfo) obj).myAddress);
  }
}
