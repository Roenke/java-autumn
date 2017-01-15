package com.spbau.bibaev.homework.torrent.client.download;

import com.spbau.bibaev.homework.torrent.client.ExitListener;
import com.spbau.bibaev.homework.torrent.client.UpdateServerInfoTask;
import com.spbau.bibaev.homework.torrent.client.api.Client;
import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.impl.AnotherClientImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfoImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.common.ClientInfo;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Vitaliy.Bibaev
 */
public class DownloadManager implements ExitListener {
  private static final Logger LOG = LogManager.getLogger(DownloadManager.class);
  private static final int TASKS_PER_FILE_LIMIT = 5;
  private static final long RESOLVER_FAILED_DELAY_SECONDS = 10;
  private final ClientStateEx myState;
  private final ScheduledExecutorService myExecutorService;
  private final Server myServer;
  private final Path myDefaultDownloadDirectory;
  private final UpdateServerInfoTask myUpdateTask;

  private final Map<Integer, BlockingQueue<Integer>> myFileId2RemainingParts = new ConcurrentHashMap<>();

  public DownloadManager(@NotNull ClientStateEx state, @NotNull InetAddress serverAddress, int port,
                         @NotNull Path defaultDownloadDirectory, @NotNull UpdateServerInfoTask updateTask) {
    myState = state;
    myServer = new ServerImpl(serverAddress, port);
    myDefaultDownloadDirectory = defaultDownloadDirectory;
    myUpdateTask = updateTask;

    myExecutorService = Executors.newScheduledThreadPool(Details.Client.DOWNLOAD_WORKERS_COUNT);
  }

  public void startDownloadAsync(int id) {
    myExecutorService.execute(new MyFileResolver(id));
  }

  public File getDefaultDirectory() {
    return myDefaultDownloadDirectory.toFile();
  }

  @Override
  public void onExit() {
    LOG.info("exit event received");
    myExecutorService.shutdownNow();
  }

  private class MyFileResolver implements Runnable {
    private final int myId;

    MyFileResolver(int fileId) {
      myId = fileId;
    }

    @Override
    public void run() {
      if (myFileId2RemainingParts.containsKey(myId)) {
        LOG.info("File with id " + myId + " already downloading.");
        return;
      }

      final Path localPath = myState.getPathById(myId);
      final ClientFileInfo localInfo = myState.getInfoById(myId);
      if (localPath != null && localInfo != null) {
        downloadToExistedFile(localInfo);
      } else {
        downloadNewFile();
      }
    }

    private void downloadNewFile() {
      try {
        final Map<Integer, FileInfo> files = myServer.list();
        if (!files.containsKey(myId)) {
          LOG.warn("File with id = " + myId + "not found");
          return;
        }

        final FileInfo info = files.get(myId);
        final Path localPath = myDefaultDownloadDirectory.resolve(info.getName());
        final Path pathToSave = getFileToSave(localPath, info);
        if (pathToSave == null) {
          LOG.error("Could not find the file to save file with id = " + myId);
          return;
        }

        myState.addNewFile(pathToSave, new ClientFileInfoImpl(myId, info.getSize(), Collections.emptyList()));
        run();
      } catch (IOException e) {
        LOG.error("Cannot load files on the server. Try again after " + RESOLVER_FAILED_DELAY_SECONDS + " seconds");
        myExecutorService.schedule(this, RESOLVER_FAILED_DELAY_SECONDS, TimeUnit.SECONDS);
      }
    }

    private void downloadToExistedFile(@NotNull ClientFileInfo localInfo) {
      final int partCount = Details.partCount(localInfo.getSize());
      final Set<Integer> loadedParts = localInfo.getParts();
      final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(partCount - loadedParts.size());
      for (int i = 0; i < partCount; i++) {
        if (!loadedParts.contains(i)) {
          queue.add(i);
        }
      }

      if (queue.size() > 0) {
        myFileId2RemainingParts.put(myId, queue);
        final Runnable downloader = new MyDownloader(myId);
        for (int i = 0; i < TASKS_PER_FILE_LIMIT; i++) {
          myExecutorService.execute(downloader);
        }

      } else {
        LOG.info(String.format("File with id = %d already loaded", myId));
      }
    }

    @Nullable
    private Path getFileToSave(@Nullable Path path, @NotNull FileInfo info) {
      if (path == null) {
        LOG.info("No local file for file with id = " + myId + ". Create a new one");
        path = myDefaultDownloadDirectory.resolve(info.getName());
      }

      if (path.toFile().exists()) {
        return canSaveToExistedFile(path, info) ? path : null;
      }

      return allocateNewFile(path, info) ? path : null;
    }

    private boolean allocateNewFile(@NotNull Path path, @NotNull FileInfo info) {
      try (RandomAccessFile f = new RandomAccessFile(path.toAbsolutePath().toString(), "rw")) {
        f.setLength(info.getSize());
        return true;
      } catch (FileNotFoundException e) {
        LOG.error("Cannot create file " + path.toAbsolutePath() + " for file with id = " + myId, e);
      } catch (IOException e) {
        LOG.error(String.format("Cannot resize file %s, to %d for file with id = %d", path.toAbsolutePath(),
            info.getSize(), myId));
      }

      return false;
    }

    private boolean canSaveToExistedFile(@NotNull Path path, @NotNull FileInfo info) {
      if (FileUtils.sizeOf(path.toFile()) == info.getSize()) {
        LOG.info(String.format("Download file with id = %d to existed file %s", myId,
            path.toAbsolutePath()));
        return true;
      }

      LOG.error("Could not save file with id = " + myId + " to " + path.toAbsolutePath() +
          ". File has another size");

      return false;
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
      final Integer partNumber = queue == null ? null : queue.poll();

      if (partNumber == null) {
        LOG.info("Loading for file with id = " + myId + " completed");
        myFileId2RemainingParts.remove(myId);
        return;
      }

      final Path path = myState.getPathById(myId);
      if (path == null || !path.toFile().exists()) {
        LOG.error("File with id = " + myId + " not existed");
        myFileId2RemainingParts.remove(myId);
        return;
      }

      boolean success = false;
      try {
        final List<ClientInfo> sources = myServer.sources(myId);
        for (ClientInfo clientInfo : sources) {
          final Client client = new AnotherClientImpl(InetAddress.getByAddress(clientInfo.getIp()),
              clientInfo.getPort());
          final List<Integer> parts = client.stat(myId);
          if (parts.contains(partNumber)) {
            final boolean loadingResult = client.get(myId, partNumber, path);
            if (loadingResult) {
              success = true;
              LOG.info("loading for part #" + partNumber + " of file with id = " + myId + " completed.");
              final ClientFileInfo fileInfoBefore = myState.getInfoById(myId);
              if (fileInfoBefore != null && fileInfoBefore.getParts().isEmpty()) {
                myUpdateTask.startOnceAsync();
              }

              myState.addFilePart(path, partNumber);

              final ClientFileInfo info = myState.getInfoById(myId);
              if (info != null && info.isLoaded()) {
                myFileId2RemainingParts.remove(myId);
              }
              break;
            }
          }

        }
      } catch (IOException e) {
        LOG.error("Downloading part #" + partNumber + " in file " + myId + " failed.", e);
        queue.add(partNumber);
        myExecutorService.schedule(this, 5, TimeUnit.SECONDS);
        return;
      }

      if (!success) {
        LOG.warn("No available parts on another clients for file ");
        queue.add(partNumber);
        myExecutorService.schedule(this, 5, TimeUnit.SECONDS);
      } else {
        myExecutorService.schedule(this, 1, TimeUnit.MILLISECONDS);
      }
    }
  }
}
