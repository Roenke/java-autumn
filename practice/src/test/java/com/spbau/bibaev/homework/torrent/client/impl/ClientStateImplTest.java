package com.spbau.bibaev.homework.torrent.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.client.api.ClientState;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientStateImplTest extends TemporaryFolder {
  @Test
  public void serialize() throws IOException {
    final ClientStateImpl instance = new ClientStateImpl(Collections.emptyMap());

    final Path path1 = Paths.get("file1");
    final Path path2 = Paths.get("file2");
    instance.addNewFile(path1, new ClientFileInfoImpl(1, 10 * Details.FILE_PART_SIZE, Arrays.asList(1, 2, 3, 4, 5)));
    instance.addNewFile(path2, new ClientFileInfoImpl(2, 8 * Details.FILE_PART_SIZE, Arrays.asList(1, 2, 3)));

    final ObjectMapper mapper = new ObjectMapper();
    final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(instance);

    System.out.println(json);

    assertTrue(json.contains("file1"));
    assertTrue(json.contains("files"));
    assertTrue(json.contains("5"));

    final ClientState state = mapper.readValue(json, ClientStateImpl.class);

    assertEquals(2, state.getFiles().size());
    assertEquals((Object) 5, state.getFile2Info().get(path1).getParts().size());
  }

  @Test
  public void addSameFileTest() {
    final ClientStateImpl instance = new ClientStateImpl(Collections.emptyMap());
    final Path path = Paths.get("File");

    assertTrue(instance.addNewFile(path, new ClientFileInfoImpl(1, 200, Collections.emptyList())));
    assertFalse(instance.addNewFile(path, new ClientFileInfoImpl(1, 200, Collections.emptyList())));

    final Path otherPath = Paths.get("file2");
    assertFalse(instance.addNewFile(otherPath, new ClientFileInfoImpl(1, 2000, Collections.emptyList())));
  }

  @Test
  public void listenerTest() {
    final ClientStateImpl instance = new ClientStateImpl(Collections.emptyMap());

    final AtomicBoolean called = new AtomicBoolean(false);
    instance.addStateModifiedListener(state -> called.set(true));

    instance.getIds();
    instance.getInfoById(0);
    instance.getPathById(0);
    instance.getFile2Info();
    instance.getFiles();
    assertFalse(called.get());

    final Path path = Paths.get("file");
    instance.addNewFile(path, new ClientFileInfoImpl(1, 100 * Details.FILE_PART_SIZE, Collections.emptyList()));
    assertTrue(called.get());
    called.set(false);

    instance.addFilePart(path, 0);
    assertTrue(called.get());

    called.set(false);
    instance.addFilePart(path, 0);
    assertFalse(called.get());
  }
}