package com.spbau.bibaev.homework.torrent.client.ui;

import com.spbau.bibaev.homework.torrent.client.UpdateServerInfoTask;
import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.download.DownloadManager;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfoImpl;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Vitaliy.Bibaev
 */
public class MainWindow extends JFrame {

  private final ServerFilesView myServerView;
  private final LocalFilesView myLocalFiles;
  private final Server myServer;
  private final ClientStateEx myState;
  private final UpdateServerInfoTask myUpdateTask;

  public MainWindow(@NotNull DownloadManager downloadManager, @NotNull Server server,
                    @NotNull ClientStateEx state, @NotNull UpdateServerInfoTask updateTask) {
    super("Torrent client " + downloadManager.getDefaultDirectory()
        .toPath().toAbsolutePath().normalize().toString());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    myServer = server;
    myState = state;
    myServerView = new ServerFilesView();
    myLocalFiles = new LocalFilesView();
    myUpdateTask = updateTask;

    JButton loadButton = new JButton("Download Selected Files");
    JButton refreshButton = new JButton("Refresh");
    final JPanel serverButtonsPane = new JPanel(new GridLayout());
    serverButtonsPane.add(loadButton);
    serverButtonsPane.add(refreshButton);

    myLocalFiles.setFiles(myState.getFile2Info());
    myState.addStateModifiedListener(state1 -> myLocalFiles.setFiles(state1.getFile2Info()));

    loadButton.addActionListener(e -> myServerView.getSelectedFileIds().forEach(downloadManager::startDownloadAsync));
    refreshButton.addActionListener(e -> new RefreshWorker().execute());
    refreshButton.doClick();

    JButton uploadFileButton = new JButton("Upload New File");

    uploadFileButton.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setMultiSelectionEnabled(false);
      chooser.setCurrentDirectory(downloadManager.getDefaultDirectory());
      if (chooser.showDialog(this, "Upload") == JFileChooser.APPROVE_OPTION) {
        final File selectedFile = chooser.getSelectedFile();
        new UploadWorker(selectedFile.toPath()).execute();
      }
    });

    final JPanel localButtonsPane = new JPanel(new GridLayout());
    localButtonsPane.add(uploadFileButton);

    final JPanel serverPanel = new JPanel(new BorderLayout());
    final JPanel localPanel = new JPanel(new BorderLayout());

    serverPanel.add(myServerView, BorderLayout.CENTER);
    serverPanel.add(serverButtonsPane, BorderLayout.SOUTH);

    localPanel.add(myLocalFiles, BorderLayout.CENTER);
    localPanel.add(localButtonsPane, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, serverPanel, localPanel);

    getContentPane().add(splitPane);
    pack();
  }

  private class RefreshWorker extends SwingWorker<Void, Void> {
    volatile Map<Integer, FileInfo> myResult;

    @Override
    protected void done() {
      if (myResult != null) {
        myServerView.setFiles(myResult);
      }
    }

    @Override
    protected Void doInBackground() throws Exception {
      myResult = new ConcurrentHashMap<>(myServer.list());
      return null;
    }
  }

  private class UploadWorker extends SwingWorker<Void, Void> {
    final Path myPath;
    volatile ClientFileInfo myInfo;

    UploadWorker(@NotNull Path path) {
      super();
      myPath = path;
    }

    @Override
    protected void done() {
      new RefreshWorker().execute();
      myUpdateTask.startOnceAsync();
    }

    @Override
    protected Void doInBackground() throws Exception {
      final long size = FileUtils.sizeOf(myPath.toFile());
      final int id = myServer.upload(new FileInfo(myPath.getFileName().toString(), size));
      final List<Integer> parts = IntStream.iterate(0, i -> i + 1).limit(Details.partCount(size))
          .boxed().collect(Collectors.toList());
      myInfo = new ClientFileInfoImpl(id, size, parts);
      myState.addNewFile(myPath, myInfo);

      return null;
    }
  }
}
