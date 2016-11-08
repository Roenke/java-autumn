package com.spbau.bibaev.homework.torrent.client;

import com.spbau.bibaev.homework.torrent.client.api.Client;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.impl.AnotherClientImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Vitaliy.Bibaev
 */
public class DownloadManager {
  private static final Logger LOG = LogManager.getLogger(DownloadManager.class);
  private static final int TASKS_PER_FILE_LIMIT = 5;
  private final ClientStateEx myState;
  private final ScheduledExecutorService myExecutorService;
  private final InetAddress myServerAddress;
  private final int myServerPort;

  private final Set<Integer> myFilesInProgress = new CopyOnWriteArraySet<>();
  private final Map<Integer, BlockingQueue<Integer>> myFileId2RemainingParts = new ConcurrentHashMap<>();

  public DownloadManager(@NotNull ClientStateEx state, @NotNull InetAddress serverAddress, int port) {
    myState = state;
    myServerAddress = serverAddress;
    myServerPort = port;
    myExecutorService = Executors.newScheduledThreadPool(Details.Client.DOWNLOADERS_COUNT);
  }

  public boolean startDownloadAsync(int id) {
    if (myFilesInProgress.contains(id)) {
      LOG.info("File with id " + id + " already downloading.");
      return false;
    }

    final ClientFileInfo info = myState.getInfoById(id);

    myExecutorService.execute(new MyFileResolver(id));

    final long size = info.getSize();
    final int parts = (int) Math.ceil((double) size / Details.FILE_PART_SIZE);

    final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
    final Set<Integer> downloadedParts = info.getParts();
    for (int i = 0; i < parts; i++) {
      if (!downloadedParts.contains(i)) {
        queue.add(i);
      }
    }

    final boolean isNeedToDownload = !queue.isEmpty();
    if (isNeedToDownload) {
      myFilesInProgress.add(id);
      myFileId2RemainingParts.put(id, queue);
      for (int i = 0; i < TASKS_PER_FILE_LIMIT; i++) {
        myExecutorService.schedule(new MyDownloader(id), 0, TimeUnit.MILLISECONDS);
      }
    }

    return isNeedToDownload;
  }

  public Collection<Integer> getFilesInProcess() {
    return Collections.unmodifiableCollection(myFilesInProgress);
  }

  private class MyFileResolver implements Runnable {
    private final int myId;

    public MyFileResolver(int fileId) {
      myId = fileId;
    }

    @Override
    public void run() {
      Server server = new ServerImpl(myServerAddress, myServerPort);
      try {
        final Map<Integer, FileInfo> files = server.list();
        if (!files.containsKey(myId)) {

        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private class MyDownloader implements Runnable {
    private final int myId;

    MyDownloader(int id) {
      myId = id;
    }

    @Override
    public void run() {
      final BlockingQueue<Integer> queue = myFileId2RemainingParts.getOrDefault(myId, null);
      Integer partNumber = queue == null ? null : queue.poll();

      if (partNumber == null) {
        LOG.info("Loading for file with id = " + myId + " completed");
        myFileId2RemainingParts.remove(myId);
        return;
      }

      final Path path = myState.getPathById(myId);
      if (path == null || !path.toFile().exists()) {
        LOG.error("File with id = " + myId + " not existed");
        return;
      }

      Server server = new ServerImpl(myServerAddress, myServerPort);
      try {
        final List<ClientInfo> sources = server.sources(myId);
        for (ClientInfo clientInfo : sources) {
          final Client client = new AnotherClientImpl(InetAddress.getByAddress(clientInfo.getIp()),
              clientInfo.getPort());
          final List<Integer> parts = client.stat(myId);
          if (parts.contains(partNumber)) {
            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "w")) {
              file.seek(partNumber * Details.FILE_PART_SIZE);
              final boolean loadingResult = client.get(myId, partNumber, file);
              if (loadingResult) {
                LOG.info("loading for part #" + partNumber + " of file with id = " + myId + " completed.");
                myState.addFilePart(path, partNumber);
                myFilesInProgress.remove(myId);
                break;
              }
            }
          }

          myExecutorService.schedule(this, 0, TimeUnit.MILLISECONDS);
        }
      } catch (IOException e) {
        LOG.error("Downloading part #" + partNumber + " in file " + myId + " failed.", e);
        myExecutorService.schedule(this, 5, TimeUnit.SECONDS);
      }
    }
  }
}
