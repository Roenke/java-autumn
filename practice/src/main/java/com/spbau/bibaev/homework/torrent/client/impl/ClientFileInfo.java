package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spbau.bibaev.homework.torrent.common.Details;
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

  @JsonIgnore
  public boolean isLoaded() {
    return myParts.size() * Details.FILE_PART_SIZE >= mySize;
  }

  public boolean addPart(int index) {
    if (index * Details.FILE_PART_SIZE < mySize) {
      return myParts.add(index);
    }

    return false;
  }

  @JsonProperty("parts")
  public Set<Integer> getParts() {
    return Collections.unmodifiableSet(myParts);
  }
}
