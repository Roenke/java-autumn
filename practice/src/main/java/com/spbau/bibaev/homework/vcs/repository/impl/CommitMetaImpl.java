package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.CommitMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class CommitMetaImpl implements CommitMeta {
  private final String myAuthor;
  private final Date myDate;
  private final String myHashCode;
  private final String myMessage;

  public CommitMetaImpl(@NotNull String hashCode, @NotNull String message, @NotNull String author, @NotNull Date date) {
    myAuthor = author;
    myDate = date;
    myHashCode = hashCode;
    myMessage = message;
  }

  @Override
  public String getId() {
    return myHashCode;
  }

  @Override
  public String getAuthor() {
    return myAuthor;
  }

  @Override
  public Date getDate() {
    return myDate;
  }

  @Override
  public String getHashcode() {
    return myHashCode;
  }

  @Override
  public String getMessage() {
    return myMessage;
  }
}
