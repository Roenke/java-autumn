package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientStateImpl implements ClientStateEx {
  private final Map<Path, ClientFileInfo> myPath2Info;
  private final Map<Integer, Path> myId2FileIndex;

  @JsonCreator
  public ClientStateImpl(@JsonProperty("files") @NotNull Map<String, ClientFileInfo> files) {
    myPath2Info = new ConcurrentHashMap<>();
    myId2FileIndex = new ConcurrentHashMap<>();

    for (String path : files.keySet()) {
      Path p = Paths.get(path);
      myPath2Info.put(p, files.get(path));
      myId2FileIndex.put(files.get(path).getId(), p);
    }
  }

  @Override
  public Collection<Path> getFiles() {
    return Collections.unmodifiableCollection(myPath2Info.keySet());
  }

  @JsonProperty("files")
  @Override
  public Map<Path, ClientFileInfo> getFile2Info() {
    return Collections.unmodifiableMap(myPath2Info);
  }

  @Override
  public Path getPathById(int id) {
    return myId2FileIndex.get(id);
  }

  @Override
  public ClientFileInfo getInfoById(int id) {
    return myPath2Info.get(myId2FileIndex.get(id));
  }

  @Override
  public void addFilePart(@NotNull Path file, int part) {
    myPath2Info.get(file).addPart(part);
  }

  @Override
  public void addNewFile(@NotNull Path file, @NotNull ClientFileInfo info) {
    assert !myPath2Info.containsKey(file);
    assert !myId2FileIndex.containsKey(info.getId());
    myPath2Info.put(file, info);
    myId2FileIndex.put(info.getId(), file);
  }
}
