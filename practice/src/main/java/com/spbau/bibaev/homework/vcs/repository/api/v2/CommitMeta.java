package com.spbau.bibaev.homework.vcs.repository.api.v2;

import java.io.Serializable;
import java.util.Date;

public interface CommitMeta extends Serializable {
  String getId();

  String getAuthor();

  Date getDate();

  String getHashcode();
}
