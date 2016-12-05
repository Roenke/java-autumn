package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import org.junit.Assert;
import org.junit.Test;

import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class BaseUdpServerTest {
  private static final int THREAD_COUNT = 50;
  private static final int ITERATION_COUNT = 10;

  private final int[] myData = new Random().ints().limit(1000).toArray();

  @Test
  public void sortingTest() throws UnknownHostException, SocketException, InterruptedException, BrokenBarrierException {
    new Thread(getServer()).start();
    final InetAddress localAddress = InetAddress.getLocalHost();
    final int port = getPort();
    Thread[] threads = new Thread[THREAD_COUNT];

    CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT + 1);
    final int[] sorted = myData.clone();
    Arrays.sort(sorted);
    AtomicBoolean ok = new AtomicBoolean(true);
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads[i] = new Thread(() -> {
        byte[] localBuffer = new byte[1024 * 50]; // 50kb
        try {
          final DatagramSocket socket = new DatagramSocket();
          socket.setSoTimeout(5000);
          barrier.await();
          for (int j = 0; j < ITERATION_COUNT; j++) {
            final DatagramPacket datagramPacket = new DatagramPacket(localBuffer, localBuffer.length,
                localAddress, port);
            DataUtils.write(myData, datagramPacket, localBuffer);
            socket.send(datagramPacket);
            socket.receive(datagramPacket);
            final int[] result = DataUtils.read(datagramPacket);

            Assert.assertArrayEquals(sorted, result);
          }
        } catch (Exception e) {
          e.printStackTrace();
          ok.set(false);
        }
      });
      threads[i].start();
    }

    barrier.await();
    for (Thread thread : threads) {
      thread.join();
    }

    Assert.assertTrue(ok.get());
  }

  protected abstract int getPort();

  protected abstract Runnable getServer();
}
