package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientStateImplTest {
  @Test
  public void serialize() throws IOException {
    final ClientStateImpl instance = new ClientStateImpl(Collections.emptyMap());

    Path path1 = Paths.get("file1");
    Path path2 = Paths.get("file2");
    instance.addNewFile(path1, new ClientFileInfo(1, 10, Arrays.asList(1, 2, 3, 4, 5)));
    instance.addNewFile(path2, new ClientFileInfo(2, 20, Arrays.asList(1, 2, 3)));

    final ObjectMapper mapper = new ObjectMapper();
    final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(instance);

    System.out.println(json);

    assertTrue(json.contains("file1"));
    assertTrue(json.contains("files"));
    assertTrue(json.contains("20"));
    assertTrue(json.contains("5"));

    ClientState state = mapper.readValue(json, ClientStateImpl.class);

    assertEquals(2, state.getFiles().size());
    assertEquals((Object) 5, state.getFile2Info().get(path1).getParts().size());
  }
}