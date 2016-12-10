package com.spbau.bibaev.benchmark.client.runner;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy.Bibaev
 */
public class TcpBenchmarkClient extends BenchmarkClient {
  private final int[] myData;
  private final int myDelay;
  private final int myIterationCount;
  private final boolean myHoldConnection;
  private final InetAddress myServerAddress;
  private final int myServerPort;

  TcpBenchmarkClient(int dataSize, int delayMs, int iterationCount,
                     @NotNull InetAddress serverAddress, int serverPort,
                     boolean holdConnection) {
    super();
    myData = new Random().ints().limit(dataSize).toArray();
    myDelay = delayMs;
    myIterationCount = iterationCount;
    myHoldConnection = holdConnection;
    myServerAddress = serverAddress;
    myServerPort = serverPort;
  }

  @Override
  public void start() throws Exception {
    if (myHoldConnection) {
      doWithHoldConnection();
    } else {
      doWithClosingConnection();
    }
  }

  private void doWithClosingConnection() throws IOException, InterruptedException {
    for (int i = 0; i < myIterationCount; i++) {
      try (final Socket socket = new Socket(myServerAddress, myServerPort);
           final InputStream is = socket.getInputStream();
           final OutputStream os = socket.getOutputStream()) {
        query(is, os);
      }

      TimeUnit.MILLISECONDS.sleep(myDelay);
    }
  }

  private void doWithHoldConnection() throws IOException, InterruptedException {
    try (final Socket socket = new Socket(myServerAddress, myServerPort);
         final InputStream is = socket.getInputStream();
         final OutputStream os = socket.getOutputStream()) {
      for (int i = 0; i < myIterationCount; i++) {
        query(is, os);
        TimeUnit.MILLISECONDS.sleep(myDelay);
      }
    }
  }

  private void query(@NotNull InputStream is, @NotNull OutputStream os) throws IOException {
    DataUtils.write(DataUtils.toMessage(myData), os);
    final int[] result = DataUtils.unbox(MessageProtos.Array.parseFrom(DataUtils.readData(is)));
    assertSorted(result);
  }
}
