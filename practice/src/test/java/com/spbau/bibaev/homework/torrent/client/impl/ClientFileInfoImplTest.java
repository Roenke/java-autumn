package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientFileInfoImplTest {
  @Test
  public void serialize() throws IOException {
    final ClientFileInfoImpl info = new ClientFileInfoImpl(1, 100, Arrays.asList(1, 2, 3));
    final ObjectMapper mapper = new ObjectMapper();
    final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(info);
    System.out.println(json);

    assertTrue(json.contains("id"));
    assertTrue(json.contains("size"));
    assertTrue(json.contains("parts"));

    final ClientFileInfoImpl after = mapper.readValue(json, ClientFileInfoImpl.class);
    assertEquals(after.getId(), info.getId());
    assertEquals(after.getSize(), info.getSize());
    assertEquals(after.getParts(), info.getParts());
  }

  @Test
  public void addDuplicatedParts() {
    final ClientFileInfoImpl info = new ClientFileInfoImpl(1, Details.FILE_PART_SIZE * 11, Arrays.asList(1, 2, 3, 4));

    assertEquals(4, info.getParts().size());
    info.addPart(10);
    assertEquals(5, info.getParts().size());
    info.addPart(10);
    assertEquals(5, info.getParts().size());
  }

  @Test
  public void testAddOutOfRangePartNumber() {
    final ClientFileInfoImpl info = new ClientFileInfoImpl(1, Details.FILE_PART_SIZE * 4, Arrays.asList(1, 2, 3, 4));

    assertFalse(info.addPart(2));
    assertFalse(info.addPart(10));
  }

  @Test
  public void loadedEq() {
    final ClientFileInfoImpl info = new ClientFileInfoImpl(1, Details.FILE_PART_SIZE * 4, Arrays.asList(0, 1, 2, 3));
    assertTrue(info.isLoaded());
  }

  @Test
  public void loadedGt() {
    final ClientFileInfoImpl info = new ClientFileInfoImpl(1, Details.FILE_PART_SIZE * 4 + 1, Arrays.asList(0, 1, 2));
    assertFalse(info.isLoaded());
    info.addPart(3);
    assertFalse(info.isLoaded());
    info.addPart(4);
    assertTrue(info.isLoaded());
  }

  @Test
  public void unmodifiedPartsReturns() {
    final ClientFileInfoImpl info = new ClientFileInfoImpl(1, 2000, Arrays.asList(1, 2, 3, 4));

    boolean thrown = false;
    try{
      info.getParts().add(100);
    }
    catch (Throwable ignored) {
      thrown = true;
    }

    assertTrue(thrown || info.getParts().size() == 4);
  }

}