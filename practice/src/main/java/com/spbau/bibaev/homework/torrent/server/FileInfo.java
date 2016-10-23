package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}
