package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class ServerState {
  private final Map<Integer, FileInfo> myId2FileInfo;

  @JsonCreator
  public ServerState(@JsonProperty("files") Map<Integer, FileInfo> map) {
    myId2FileInfo = map;
  }

  @JsonProperty("files")
  public Map<Integer, FileInfo> getFiles() {
    return Collections.unmodifiableMap(myId2FileInfo);
  }

  public boolean fileExists(int id) {
    return myId2FileInfo.containsKey(id);
  }

  @NotNull
  public FileInfo getInfo(int id) {
    return myId2FileInfo.getOrDefault(id, null);
  }

  public int putNewFile(FileInfo info) {
    int id = myId2FileInfo.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    myId2FileInfo.put(id, info);
    return id;
  }
}
