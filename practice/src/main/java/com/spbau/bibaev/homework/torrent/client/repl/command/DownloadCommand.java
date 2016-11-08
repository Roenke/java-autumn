package com.spbau.bibaev.homework.torrent.client.repl.command;

import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.download.DownloadManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class DownloadCommand implements UserCommand {
  private final DownloadManager myDownloadManager;

  public DownloadCommand(@NotNull DownloadManager manager) {
    myDownloadManager = manager;
  }


  @Override
  public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
    if (args.length < 2) {
      System.out.println("Specify one ore more files for downloading");
      System.out.println("Usage: " + getUsage());
      return;
    }

    final List<Integer> ids = new ArrayList<>();
    for (int i = 1; i < args.length; i++) {
      try {
        final int id = Integer.parseInt(args[i]);
        final ClientFileInfo info = state.getInfoById(id);
        if (info != null) {
          System.err.println("File " + id + " already downloading/downloaded");
          return;
        }

        ids.add(id);
      } catch (NumberFormatException e) {
        System.err.println("id must be an integer number. Found: " + args[i]);
        return;
      }
    }

    ids.forEach(myDownloadManager::startDownloadAsync);
  }

  @Override
  public String getUsage() {
    return "download id <[id]>";
  }

  @Override
  public String getDescription() {
    return "Download file by id";
  }
}
