package com.spbau.bibaev.homework.torrent.server;

/**
 * @author Vitaliy.Bibaev
 */
public interface ClientInfo {
  byte[] getIp();
  int getPort();
}
