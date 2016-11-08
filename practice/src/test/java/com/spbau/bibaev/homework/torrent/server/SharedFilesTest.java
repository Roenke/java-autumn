package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import com.spbau.bibaev.homework.torrent.server.state.SharedFiles;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Vitaliy.Bibaev
 */
public class SharedFilesTest {
  @Test
  public void serialize() throws IOException {
    Map<Integer, FileInfo> map = new HashMap<>();
    map.put(1, new FileInfo("name1", 10));
    map.put(2, new FileInfo("name2", 20));
    SharedFiles serverState = new SharedFiles(map);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(serverState);
    System.out.println(json);
    SharedFiles after = mapper.readValue(json, SharedFiles.class);

    assertNotSame(serverState, after);
    assertTrue(after.fileExists(1));
    assertTrue(after.fileExists(2));

    assertEquals(serverState.getFiles().size(), after.getFiles().size());
    assertEquals(serverState.getInfo(1).getName(), after.getInfo(1).getName());
    assertEquals(serverState.getInfo(1).getSize(), after.getInfo(1).getSize());
    assertEquals(serverState.getInfo(2).getName(), after.getInfo(2).getName());
    assertEquals(serverState.getInfo(2).getSize(), after.getInfo(2).getSize());
  }

  @Test
  public void exists() {
    Map<Integer, FileInfo> map = new HashMap<>();
    map.put(1, new FileInfo("name1", 10));
    SharedFiles serverState = new SharedFiles(map);

    assertTrue(serverState.fileExists(1));
    assertFalse(serverState.fileExists(2));
  }

  @Test
  public void getInfo() {
    Map<Integer, FileInfo> map = new HashMap<>();
    map.put(1, new FileInfo("name1", 10));
    SharedFiles serverState = new SharedFiles(map);

    assertEquals(new FileInfo("name1", 10), serverState.getInfo(1));
  }

  @Test
  public void putNewFile() {
    Map<Integer, FileInfo> map = new HashMap<>();
    map.put(1, new FileInfo("name1", 10));
    SharedFiles serverState = new SharedFiles(map);

    assertNotEquals(1, serverState.putNewFile(new FileInfo("name2", 100)));
    assertEquals(2, serverState.getFiles().size());
  }
}