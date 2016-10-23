package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ServerStateTest {
  @Test
  public void serialize() throws IOException {
    Map<Integer, FileInfo> map = new HashMap<>();
    map.put(1, new FileInfo("name1", 10));
    map.put(2, new FileInfo("name2", 20));
    ServerState serverState = new ServerState(map);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(serverState);
    System.out.println(json);
    ServerState after = mapper.readValue(json, ServerState.class);

    assertNotSame(serverState, after);
    assertTrue(after.fileExists(1));
    assertTrue(after.fileExists(2));

    assertEquals(serverState.getFiles().size(), after.getFiles().size());
    assertEquals(serverState.getInfo(1).getName(), after.getInfo(1).getName());
    assertEquals(serverState.getInfo(1).getSize(), after.getInfo(1).getSize());
    assertEquals(serverState.getInfo(2).getName(), after.getInfo(2).getName());
    assertEquals(serverState.getInfo(2).getSize(), after.getInfo(2).getSize());
  }
}