package com.spbau.bibaev.practice.nonblocking.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;

public final class Common {

  public static final int PORT = 15638;
  public static final String RESOURCES_PATH = "07-practice-nio-client/src/main/resources/";

  public static String md5(String path) throws IOException {
    try (final FileInputStream inputStream = new FileInputStream(path)) {
      return DigestUtils.md5Hex(inputStream);
    }
  }
}