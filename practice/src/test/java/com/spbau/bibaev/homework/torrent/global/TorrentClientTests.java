package com.spbau.bibaev.homework.torrent.global;

import com.spbau.bibaev.homework.torrent.client.TorrentClientServer;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.impl.AnotherClientImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ClientFileInfoImpl;
import com.spbau.bibaev.homework.torrent.client.impl.ClientStateImpl;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * @author Vitaliy.Bibaev
 */
public class TorrentClientTests {
  private static final int PORT = 20004;
  private File myFile1;
  private File myFile2;
  private ClientStateEx myState;

  private TorrentClientServer myServer;
  private Thread myServerThread;

  @Rule
  public final TemporaryFolder folderRule = new TemporaryFolder() {
    @Override
    protected void before() throws Throwable {
      super.before();
      myFile1 = newFile("file1");
      myFile2 = newFile("file2");

      String longString = CharBuffer.allocate(Details.FILE_PART_SIZE).toString().replace('\0', 'a') +
          CharBuffer.allocate(Details.FILE_PART_SIZE).toString().replace('\0', 'b') +
          CharBuffer.allocate(Details.FILE_PART_SIZE).toString().replace('\0', 'c');

      FileUtils.write(myFile1, "HelloWorld", "UTF-8");
      FileUtils.write(myFile2, longString, "UTF-8");
      final List<Integer> parts1 = Stream.iterate(0, i -> i + 1)
          .limit(Details.partCount(myFile1.length()))
          .collect(Collectors.toList());
      final List<Integer> parts2 = Stream.iterate(0, i -> i + 1)
          .limit(Details.partCount(myFile2.length()))
          .collect(Collectors.toList());

      Map<String, ClientFileInfoImpl> files = new HashMap<>();
      files.put(myFile1.toPath().toAbsolutePath().toString(), new ClientFileInfoImpl(1, myFile1.length(), parts1));
      files.put(myFile2.toPath().toAbsolutePath().toString(), new ClientFileInfoImpl(2, myFile2.length(), parts2));
      myState = new ClientStateImpl(files);
      myServer = new TorrentClientServer(PORT, myState);
      myServerThread = new Thread(() -> myServer.start());
      myServerThread.start();
    }

    @Override
    protected void after() {
      super.after();
      try {
        myServer.shutdown();
        myServerThread.join();
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  };

  @Test
  public void simpleStatRequest() throws IOException {
    final AnotherClientImpl client = new AnotherClientImpl(InetAddress.getLoopbackAddress(), PORT);
    final List<Integer> stat = client.stat(1);
    assertEquals(Details.partCount(myFile1.length()), stat.size());
  }

  @Test
  public void simpleGetRequest() throws IOException {
    final AnotherClientImpl client = new AnotherClientImpl(InetAddress.getLoopbackAddress(), PORT);
    final File out = folderRule.newFile("out");

    client.get(1, 0, out.toPath());

    final String content = FileUtils.readFileToString(out, "UTF-8");
    assertEquals("HelloWorld", content);
  }

  @Test
  public void getWithOffsetGetRequest() throws IOException {
    final AnotherClientImpl client = new AnotherClientImpl(InetAddress.getLoopbackAddress(), PORT);
    final File out = folderRule.newFile("out");

    client.get(2, 1, out.toPath());

    final String content = FileUtils.readFileToString(out, "UTF-8");
    assertEquals(3 * Details.FILE_PART_SIZE, content.length());
  }
}