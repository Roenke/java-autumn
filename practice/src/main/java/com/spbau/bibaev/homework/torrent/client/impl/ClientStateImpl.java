package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.api.StateChangedListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientStateImpl implements ClientStateEx {
  private static final Logger LOG = LogManager.getLogger(ClientStateImpl.class);

  private final Map<Path, ClientFileInfo> myPath2Info;
  private final Map<Integer, Path> myId2FileIndex;
  private final List<StateChangedListener> myListeners = new CopyOnWriteArrayList<>();

  @JsonCreator
  public ClientStateImpl(@JsonProperty("files") @NotNull Map<String, ClientFileInfoImpl> files) {
    myPath2Info = new ConcurrentHashMap<>();
    myId2FileIndex = new ConcurrentHashMap<>();

    for (String path : files.keySet()) {
      final Path p = Paths.get(path);
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

  @JsonIgnore
  @Override
  public Collection<Integer> getIds() {
    return Collections.unmodifiableCollection(myId2FileIndex.keySet());
  }

  @Nullable
  @Override
  public Path getPathById(int id) {
    return myId2FileIndex.get(id);
  }

  @Nullable
  @Override
  public ClientFileInfo getInfoById(int id) {
    Path path = myId2FileIndex.getOrDefault(id, null);
    return path == null ? null : myPath2Info.get(path);
  }

  @Override
  public void addFilePart(@NotNull Path file, int part) {
    final ClientFileInfoImpl clientFileInfo = (ClientFileInfoImpl) myPath2Info.get(file);
    if (clientFileInfo.addPart(part)) {
      fireStateChanged();
    }
  }

  @Override
  public boolean addNewFile(@NotNull Path file, @NotNull ClientFileInfo info) {
    if (myPath2Info.containsKey(file)) {
      LOG.warn(file.toAbsolutePath() + " already added");
      return false;
    }

    if (myId2FileIndex.containsKey(info.getId())) {
      LOG.warn("file with id = " + info.getId() + " already added");
      return false;
    }

    myPath2Info.put(file, info);
    myId2FileIndex.put(info.getId(), file);
    fireStateChanged();
    return true;
  }

  @Override
  public void addStateModifiedListener(@NotNull StateChangedListener listener) {
    myListeners.add(listener);
  }

  private void fireStateChanged() {
    for (StateChangedListener listener : myListeners) {
      try {
        listener.stateModified(this);
      } catch (Throwable e) {
        LOG.warn("Error in state modification listener", e);
        throw e;
      }
    }
  }
}
