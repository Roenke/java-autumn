package com.spbau.bibaev.homework.torrent.global;

import com.spbau.bibaev.homework.torrent.client.impl.ServerImpl;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import com.spbau.bibaev.homework.torrent.server.TorrentServer;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateImpl;
import com.spbau.bibaev.homework.torrent.server.state.SharedFiles;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vitaliy.Bibaev
 */
public class TorrentServerTests {
  private static final int PORT = 20003;
  private static final FileInfo FILE1 = new FileInfo("name", 100);
  private static final FileInfo FILE2 = new FileInfo("name2", 1000);
  private static final FileInfo FILE3 = new FileInfo("name3", 10000);
  private static final FileInfo FILE4 = new FileInfo("name", 100000);
  private static TorrentServer myServer;

  @BeforeClass
  public static void before() throws InterruptedException, IOException {
    Map<Integer, FileInfo> fileMap = new HashMap<>();
    fileMap.put(1, FILE1);
    fileMap.put(2, FILE2);
    fileMap.put(3, FILE3);
    fileMap.put(4, FILE4);
    SharedFiles files = new SharedFiles(fileMap);
    ServerStateImpl state = new ServerStateImpl(files);
    myServer = new TorrentServer(PORT, state);
    new Thread(() -> {
      try {
        myServer.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  @Test
  public void listRequest() throws IOException, InterruptedException {
    Thread.sleep(1000);
    ServerImpl server = new ServerImpl(InetAddress.getLoopbackAddress(), PORT);
    final Map<Integer, FileInfo> list = server.list();

    assertTrue(list.containsKey(1));
    assertTrue(list.containsKey(2));
    assertTrue(list.containsKey(3));
    assertTrue(list.containsKey(4));

    assertTrue(list.containsValue(FILE1));
    assertTrue(list.containsValue(FILE2));
    assertTrue(list.containsValue(FILE3));
    assertTrue(list.containsValue(FILE4));
  }

  @Test
  public void uploadRequest() throws IOException {
    ServerImpl server = new ServerImpl(InetAddress.getLoopbackAddress(), PORT);
    final FileInfo file = new FileInfo("myFile", 10003);
    final int id = server.upload(file);

    assertFalse(Arrays.asList(1, 2, 3, 4).contains(id));

    assertTrue(server.list().containsValue(file));
  }

  @Test
  public void updateAndSourcesRequests() throws IOException {
    ServerImpl server = new ServerImpl(InetAddress.getLoopbackAddress(), PORT);

    final boolean update = server.update(30000, Arrays.asList(1, 2, 3));

    assertTrue(update);
    assertTrue(server.sources(1).stream().filter(clientInfo -> clientInfo.getPort() == 30000).findFirst().isPresent());
    assertTrue(server.sources(2).stream().filter(clientInfo -> clientInfo.getPort() == 30000).findFirst().isPresent());
    assertTrue(server.sources(3).stream().filter(clientInfo -> clientInfo.getPort() == 30000).findFirst().isPresent());
    assertFalse(server.sources(100).stream().filter(clientInfo -> clientInfo.getPort() == 30000).findFirst().isPresent());
  }
}
