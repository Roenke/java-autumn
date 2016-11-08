package com.spbau.bibaev.homework.torrent.client.download;

/**
 * @author Vitaliy.Bibaev
 */
public interface DownloadManagerListener {
  void loadingStarted(int fileId);

  void partLoaded(int fileId, int partNumber);

  void loadingCompleted(int fileId);

  void loadingFailed(int fileId);
}
