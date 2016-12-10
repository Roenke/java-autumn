package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class FixedThreadPoolServer extends UdpServer {
  private static final int WORKERS_COUNT = Runtime.getRuntime().availableProcessors();

  private final ExecutorService myThreadPool = Executors.newFixedThreadPool(WORKERS_COUNT);

  public FixedThreadPoolServer() {
    super(Details.UdpPorts.FIXED_THREAD_POOL);
  }

  @Override
  protected void start(@NotNull DatagramSocket socket, @NotNull DatagramPacket packet) throws IOException {
    while (!socket.isClosed()) {
      socket.receive(packet);

      myThreadPool.execute(() -> {
        try {
          handle(socket, packet);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
