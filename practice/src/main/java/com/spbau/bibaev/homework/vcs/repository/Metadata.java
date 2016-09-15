package com.spbau.bibaev.homework.vcs.repository;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Metadata {
  public String currentBranch;
  public String userName;

  public static Metadata defaultMeta() {
    Metadata meta = new Metadata();
    meta.userName = "unknown";
    meta.currentBranch = "master";

    return meta;
  }
}
