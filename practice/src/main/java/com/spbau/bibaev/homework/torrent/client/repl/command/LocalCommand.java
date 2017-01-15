package com.spbau.bibaev.homework.torrent.client.repl.command;

import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy.Bibaev
 */
public class LocalCommand implements UserCommand {
  @Override
  public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
    final Map<Path, ClientFileInfo> file2Info = state.getFile2Info();
    System.out.println(String.format("%12s|%60s|%12s|%7s|%s", "ID", "Path", "Size", "Loaded", "Parts"));
    for (Path path : file2Info.keySet()) {
      final ClientFileInfo info = file2Info.get(path);
      final Set<Integer> parts = info.getParts();
      System.out.println(String.format("%12d|%60s|%12d|%7s|%s", info.getId(), path.toAbsolutePath().toString(),
          info.getSize(), info.isLoaded(), info.isLoaded() ?
              String.format("%d - %d", 0, Details.partCount(info.getSize()))
              : Arrays.toString(parts.toArray())));
    }
  }

  @Override
  public String getUsage() {
    return "local";
  }

  @Override
  public String getDescription() {
    return "Show local files available for sharing";
  }
}
