package com.spbau.bibaev.homework.torrent.client.repl.command;

import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * @author Vitaliy.Bibaev
 */
public class ListCommand implements UserCommand {
  private static final Logger LOG = LogManager.getLogger(ListCommand.class);
  private final Server myServer;

  public ListCommand(@NotNull Server server) {
    myServer = server;
  }

  @Override
  public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
    try {
      final Map<Integer, FileInfo> files = myServer.list();
      System.out.println(String.format("%12s%40s%12s", "ID", "Name", "Size"));
      for (Integer id : files.keySet()) {
        final FileInfo info = files.get(id);
        System.out.println(String.format("%12d%40s%12d b", id, info.getName(), info.getSize()));
      }
    } catch (IOException e) {
      LOG.error(e);
      System.err.println("Could not load list of files from server. ");
    }
  }

  @Override
  public String getUsage() {
    return "list";
  }

  @Override
  public String getDescription() {
    return "Show list of available files";
  }
}
