package com.spbau.bibaev.homework.torrent.client.ui;

import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.download.DownloadManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vitaliy.Bibaev
 */
public class MainWindow extends JFrame {
  private final DownloadManager myDownloadManager;
  private final ServerFilesView myServerView;
  private final LocalFilesView myLocalFiles;

  public MainWindow(@NotNull DownloadManager downloadManager, @NotNull Server server) {
    super("Torrent client ");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    myServerView = new ServerFilesView();
    myLocalFiles = new LocalFilesView();
    add(myServerView, BorderLayout.NORTH);
    add(myLocalFiles, BorderLayout.SOUTH);
    pack();

    myDownloadManager = downloadManager;
  }
}
