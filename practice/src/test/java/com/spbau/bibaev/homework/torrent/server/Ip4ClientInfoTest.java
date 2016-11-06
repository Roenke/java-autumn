package com.spbau.bibaev.homework.torrent.server;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Vitaliy.Bibaev
 */
public class Ip4ClientInfoTest {
  @Test
  public void equals() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 0);
    assertEquals(info1, info2);
  }

  @Test
  public void notEquals() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 1);
    assertNotEquals(info1, info2);
  }

  @Test
  public void hashCodesSame() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 0);
    assertEquals(info1.hashCode(), info2.hashCode());
  }

  @Test
  public void hashCodesNotSame() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 1);
    assertNotEquals(info1.hashCode(), info2.hashCode());
  }
}