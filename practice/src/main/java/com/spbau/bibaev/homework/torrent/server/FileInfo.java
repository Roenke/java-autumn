package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class FileInfo {
  private final String myName;
  private final long mySize;

  @JsonCreator
  public FileInfo(@JsonProperty("name") String name, @JsonProperty("size") long size) {
    myName = name;
    mySize = size;
  }

  @JsonProperty("name")
  public String getName() {
    return myName;
  }

  @JsonProperty("size")
  public long getSize() {
    return mySize;
  }

  @Override
  public int hashCode() {
    return Objects.hash(myName, mySize);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FileInfo)) {
      return false;
    }

    FileInfo other = (FileInfo) obj;
    return Objects.equals(myName, other.getName()) && Objects.equals(mySize, other.getSize());
  }
}
