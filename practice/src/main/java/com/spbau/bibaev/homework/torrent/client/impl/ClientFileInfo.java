package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientFileInfo {
  private final int myId;
  private final long mySize;
  private final Set<Integer> myParts = new CopyOnWriteArraySet<>();

  @JsonCreator
  public ClientFileInfo(@JsonProperty("id") int id, @JsonProperty("size") long size,
                        @JsonProperty("parts") @NotNull List<Integer> parts) {
    mySize = size;
    parts.forEach(this::addPart);
    myId = id;
  }

  @JsonProperty("id")
  public int getId() {
    return myId;
  }

  @JsonProperty("size")
  public long getSize() {
    return mySize;
  }

  public void addPart(int index) {
    myParts.add(index);
  }

  @JsonProperty("parts")
  public Set<Integer> getParts() {
    return Collections.unmodifiableSet(myParts);
  }
}
