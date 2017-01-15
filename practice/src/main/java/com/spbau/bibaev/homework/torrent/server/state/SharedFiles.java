package com.spbau.bibaev.homework.torrent.server.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SharedFiles {
  private final Map<Integer, FileInfo> myId2FileInfo;
  private final List<FilesChangedListener> myListeners = new CopyOnWriteArrayList<>();

  @JsonCreator
  public SharedFiles(@JsonProperty("files") Map<Integer, FileInfo> map) {
    myId2FileInfo = new ConcurrentHashMap<>(map);
  }

  @JsonProperty("files")
  public Map<Integer, FileInfo> getFiles() {
    return Collections.unmodifiableMap(myId2FileInfo);
  }

  public boolean fileExists(int id) {
    return myId2FileInfo.containsKey(id);
  }

  @Nullable
  public FileInfo getInfo(int id) {
    return myId2FileInfo.get(id);
  }

  public void addStateChangedListener(@NotNull FilesChangedListener listener) {
    myListeners.add(listener);
  }

  public int putNewFile(@NotNull FileInfo info) {
    final int newId = myId2FileInfo.keySet().stream().max(Integer::compare).orElse(0) + 1;
    myId2FileInfo.put(newId, info);
    fireStateChanged();
    return newId;
  }

  private void fireStateChanged() {
    for (FilesChangedListener listener : myListeners) {
      listener.stateChanged(this);
    }
  }
}
