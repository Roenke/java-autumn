package com.spbau.bibaev.benchmark.server.arch.udp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
  protected void start(@NotNull DatagramSocket socket) throws IOException {
    byte[] buffer = new byte[RECEIVE_BUFFER_SIZE];
    final DatagramPacket datagram = new DatagramPacket(buffer, RECEIVE_BUFFER_SIZE);
    while (!socket.isClosed()) {
      socket.receive(datagram);

      final MessageProtos.Array array = DataUtils.readToArray(datagram);
      final InetAddress address = datagram.getAddress();
      final int port = datagram.getPort();
      myThreadPool.execute(() -> {
        try {
          handle(socket, array, address, port);
        } catch (SocketException ignored) {

        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }

  @Override
  public void shutdown() {
    super.shutdown();
    myThreadPool.shutdown();
  }
}
