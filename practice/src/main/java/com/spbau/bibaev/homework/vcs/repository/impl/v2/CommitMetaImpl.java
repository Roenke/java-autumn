package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.CommitMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class CommitMetaImpl implements CommitMeta {
  private final String myId;
  private final String myAuthor;
  private final Date myDate;
  private final String myHashCode;

  public CommitMetaImpl(@NotNull String id, @NotNull String author, @NotNull Date date, @NotNull String hashCode) {
    myId = id;
    myAuthor = author;
    myDate = date;
    myHashCode = hashCode;
  }

  @Override
  public String getId() {
    return myId;
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
}
