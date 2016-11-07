package com.spbau.bibaev.homework.torrent.server;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Vitaliy.Bibaev
 */
public class Ip4ClientInfoTest {
  @Test
  public void equals() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    assertEquals(info1, info2);
  }

  @Test
  public void notEqualsIp() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 1, 1000);
    assertNotEquals(info1, info2);
  }

  @Test
  public void notEqualsPort() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 0, 1001);
    assertNotEquals(info1, info2);
  }

  @Test
  public void hashCodesSame() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    assertEquals(info1.hashCode(), info2.hashCode());
  }

  @Test
  public void hashCodesNotSameIp() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 1, 1000);
    assertNotEquals(info1.hashCode(), info2.hashCode());
  }
  @Test
  public void hashCodesNotSamePort() {
    Ip4ClientInfo info1 = new Ip4ClientInfo(0, 0, 0, 0, 1000);
    Ip4ClientInfo info2 = new Ip4ClientInfo(0, 0, 0, 0, 1001);
    assertNotEquals(info1.hashCode(), info2.hashCode());
  }
}