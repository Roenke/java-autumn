package com.spbau.bibaev.homework.torrent.client.repl.command;

import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.api.Server;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfoImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Vitaliy.Bibaev
 */
public class UploadCommand implements UserCommand {
  private final Server myServer;

  public UploadCommand(@NotNull InetAddress serverAddress, int serverPort) {
    myServer = new ServerImpl(serverAddress, serverPort);
  }

  @Override
  public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
    if (args.length != 2) {
      System.err.println("Wrong command format. ");
      System.out.println(getUsage());
      return;
    }

    final Path path = Paths.get(args[1]);
    if (state.getFile2Info().containsKey(path)) {
      System.err.println("File already uploaded");
      return;
    }

    if (!path.toFile().exists()) {
      System.err.println("File " + path.toAbsolutePath() + " not found");
      return;
    }

    final long size = FileUtils.sizeOf(path.toFile());
    FileInfo info = new FileInfo(path.toFile().getName(), size);
    try {
      final int id = myServer.upload(info);
      List<Integer> parts = IntStream.iterate(0, i -> i + 1).limit(Details.partCount(size))
          .boxed().collect(Collectors.toList());
      state.addNewFile(path, new ClientFileInfoImpl(id, size, parts));
    } catch (IOException e) {
      System.err.println("Could not upload the file. " + e);
    }
  }

  @Override
  public String getUsage() {
    return "upload path";
  }

  @Override
  public String getDescription() {
    return "Upload file to server and make it available to share";
  }
}
