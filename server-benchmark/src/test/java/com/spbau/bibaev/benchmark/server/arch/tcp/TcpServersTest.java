package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class TcpServersTest {
  private final static int THREADS_COUNT = 15;
  private final static int ITERATIONS_COUNT = 15;
  private final int[] myData = new Random().ints().limit(1000).toArray();

  @Test
  public void sortingTest() throws BrokenBarrierException, InterruptedException, IOException {
    TcpServer server = getServer();
    new Thread(server).start();
    CyclicBarrier barrier = new CyclicBarrier(THREADS_COUNT + 1);
    Thread[] threads = new Thread[THREADS_COUNT];
    AtomicBoolean ok = new AtomicBoolean(true);
    for (int i = 0; i < THREADS_COUNT; i++) {
      final Thread thread = new Thread(() -> {
        try {
          barrier.await();
          for (int k = 0; k < ITERATIONS_COUNT; k++) {
            final Socket socket = new Socket(InetAddress.getLocalHost(), getPort());
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            DataUtils.write(myData, os);
            final int[] result = DataUtils.readArray(is);
            assertEquals(result.length, myData.length);

            for (int j = 1; j < result.length; j++) {
              assertTrue(result[j - 1] <= result[j]);
            }

            socket.close();
          }

        } catch (IOException | InterruptedException | BrokenBarrierException e) {
          ok.set(false);
        }
      });
      threads[i] = thread;
      thread.start();
    }

    barrier.await();
    for (Thread thread : threads) {
      thread.join();
    }

    server.shutdown();

    assertTrue(ok.get());
  }

  void permanentConnectionTest() throws BrokenBarrierException, InterruptedException, IOException {
    TcpServer server = getServer();
    new Thread(server).start();
    CyclicBarrier barrier = new CyclicBarrier(THREADS_COUNT + 1);
    Thread[] threads = new Thread[THREADS_COUNT];
    AtomicBoolean ok = new AtomicBoolean(true);
    for (int i = 0; i < THREADS_COUNT; i++) {
      final Thread thread = new Thread(() -> {
        try {
          barrier.await();
          try(final Socket socket = new Socket(InetAddress.getLocalHost(), getPort());
              OutputStream os = socket.getOutputStream();
              InputStream is = socket.getInputStream()) {
            for (int k = 0; k < ITERATIONS_COUNT; k++) {
              DataUtils.write(myData, os);
              final int[] result = DataUtils.readArray(is);
              assertEquals(result.length, myData.length);

              for (int j = 1; j < result.length; j++) {
                assertTrue(result[j - 1] <= result[j]);
              }
            }
          }

        } catch (IOException | InterruptedException | BrokenBarrierException e) {
          ok.set(false);
        }
      });
      threads[i] = thread;
      thread.start();
    }

    barrier.await();
    for (Thread thread : threads) {
      thread.join();
    }

    server.shutdown();

    assertTrue(ok.get());
  }

  public abstract int getPort();

  public abstract TcpServer getServer();
}