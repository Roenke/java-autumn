package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorage {
  private final Map<Integer, FileInfo> myId2FileInfo;

  @JsonCreator
  public FileStorage(@JsonProperty("files") Map<Integer, FileInfo> map) {
    myId2FileInfo = new ConcurrentHashMap<>(map);
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
    return myId2FileInfo.get(id);
  }

  public int putNewFile(FileInfo info) {
    final int newId = myId2FileInfo.keySet().stream().max(Integer::compare).orElse(0) + 1;
    myId2FileInfo.put(newId, info);
    return newId;
  }
}
