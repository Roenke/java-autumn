package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientFileInfoTest {
  @Test
  public void serialize() throws IOException {
    final ClientFileInfo info = new ClientFileInfo(1, 100, Arrays.asList(1, 2, 3));
    final ObjectMapper mapper = new ObjectMapper();
    final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(info);
    System.out.println(json);

    assertTrue(json.contains("id"));
    assertTrue(json.contains("size"));
    assertTrue(json.contains("parts"));

    final ClientFileInfo after = mapper.readValue(json, ClientFileInfo.class);
    assertEquals(after.getId(), info.getId());
    assertEquals(after.getSize(), info.getSize());
    assertEquals(after.getParts(), info.getParts());
  }
}