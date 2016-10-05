package com.spbau.bibaev.homework.vcs.repository.impl;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Deprecated
@XmlRootElement
class FileDescriptor {
  @SuppressWarnings("unused")
  FileDescriptor() {
  }

  FileDescriptor(@NotNull String hash, long off, long len, boolean read, boolean write, boolean execute) {
    fileHash = hash;
    offset = off;
    length = len;
    canRead = read;
    canWrite = write;
    canExecute = execute;
  }

  @XmlElement
  String fileHash;
  @XmlElement
  long offset;
  @XmlElement
  long length;

  @XmlElement
  boolean canRead;
  @XmlElement
  boolean canWrite;
  @XmlElement
  boolean canExecute;
}
