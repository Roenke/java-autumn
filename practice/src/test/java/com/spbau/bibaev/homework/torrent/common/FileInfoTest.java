package com.spbau.bibaev.homework.torrent.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.server.FileInfo;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class FileInfoTest {
  @Test
  public void serialize() throws IOException {
    ObjectMapper mapped = new ObjectMapper();
    FileInfo fileInfo = new FileInfo("java.pdf", 43562);
    String json = mapped.writerWithDefaultPrettyPrinter().writeValueAsString(fileInfo);
    System.out.println(json);
    FileInfo after = mapped.readValue(json, FileInfo.class);

    assertNotSame(fileInfo, after);
    assertEquals(fileInfo.getName(), after.getName());
    assertEquals(fileInfo.getSize(), after.getSize());
  }
}
