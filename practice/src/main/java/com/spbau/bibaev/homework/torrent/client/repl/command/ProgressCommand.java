package com.spbau.bibaev.homework.torrent.client.repl.command;

import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaliy.Bibaev
 */
public class ProgressCommand implements UserCommand {
  @Override
  public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
    final Map<Path, ClientFileInfo> file2Info = state.getFile2Info();
    System.out.println(String.format("%12s|%20s|%s", "ID ", " Progress ", " Parts "));
    for (Path path : file2Info.keySet()) {
      final ClientFileInfo info = file2Info.get(path);
      final Set<Integer> parts = info.getParts();
      long remain = info.getSize() - parts.size() * Details.FILE_PART_SIZE;
      if (remain > 0) {
        final int partsCount = Details.partCount(info.getSize());
        System.out.println(String.format("%12d|%20f.1%%|%d/%d", info.getId(), 100. * remain / partsCount, remain, partsCount));
      }
    }
  }

  @Override
  public String getUsage() {
    return "progress";
  }

  @Override
  public String getDescription() {
    return "Show progress for current downloading tasks";
  }
}
