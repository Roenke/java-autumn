package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;
import com.spbau.bibaev.benchmark.server.arch.ServerWithStatistics;
import com.spbau.bibaev.benchmark.server.stat.ServerStatistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
public class SingleServerStarter {
  public static final int PORT = 20053;

  public static void main(String[] args) throws InterruptedException, IOException {
    final ServerSocket socket = new ServerSocket(PORT);
    while (!socket.isClosed()) {
      try (final Socket clientSocket = socket.accept();
           final DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
           final DataInputStream is = new DataInputStream(clientSocket.getInputStream())) {
        final int archIndex = is.readInt();
        final ServerArchitectureDescription description = Details.availableArchitectures().get(archIndex);
        final ServerWithStatistics server = ServerFactory.getServerByDefaultPort(description.getDefaultServerPort());
        assert server != null;
        final Thread serverThread = new Thread(server);
        serverThread.start();
        os.writeInt(0);

        final int zero = is.readInt();
        assert 0 == zero;
        server.shutdown();
        serverThread.join();
        final ServerStatistics statistics = server.getStatistics();

        os.writeLong(statistics.getQueryProcessingMetric());
        os.writeLong(statistics.getClientProcessingMetric());
      }
    }
  }
}
