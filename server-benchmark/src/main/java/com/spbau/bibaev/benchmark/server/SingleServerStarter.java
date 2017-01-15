package com.spbau.bibaev.benchmark.server;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.Protocol;
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
    final ServerWithStatistics asyncServer =
        ServerFactory.getServerByDefaultPort(Details.TcpPorts.ASYNC_PROCESSING);
    final ServerWithStatistics nonblockingServer =
        ServerFactory.getServerByDefaultPort(Details.TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING);
    new Thread(asyncServer).start();
    new Thread(nonblockingServer).start();

    final ServerSocket socket = new ServerSocket(PORT);
    while (!socket.isClosed()) {
      try (final Socket clientSocket = socket.accept();
           final DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
           final DataInputStream is = new DataInputStream(clientSocket.getInputStream())) {
        final int archIndex = is.readInt();
        final ServerArchitectureDescription description = Details.availableArchitectures().get(archIndex);
        final ServerWithStatistics server;
        Thread serverThread = null;
        if (description.getDefaultServerPort() == Details.TcpPorts.ASYNC_PROCESSING) {
          server = asyncServer;
        } else if (description.getDefaultServerPort() == Details.TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING) {
          server = nonblockingServer;
        } else {
          server = ServerFactory.getServerByDefaultPort(description.getDefaultServerPort());
          serverThread = new Thread(server);
          serverThread.start();
          // waiting for server start to listen
          if (description.getProtocol().equals(Protocol.UDP)) {
            Thread.sleep(100);
          }
        }

        assert server != null;
        server.clearStatistics();

        os.writeInt(0);

        final int zero = is.readInt();
        assert 0 == zero;
        if (serverThread != null) {
          server.shutdown();
          serverThread.join();
        }

        final ServerStatistics statistics = server.getStatistics();

        os.writeLong(statistics.getQueryProcessingMetric());
        os.writeLong(statistics.getClientProcessingMetric());
      } catch (IOException e) {
        System.err.println("Something went wrong:" + e.toString());
      }
    }
  }
}
