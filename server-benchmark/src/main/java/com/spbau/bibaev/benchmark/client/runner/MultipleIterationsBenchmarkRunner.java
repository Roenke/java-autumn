package com.spbau.bibaev.benchmark.client.runner;

import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.server.SingleServerStarter;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy.Bibaev
 */
public class MultipleIterationsBenchmarkRunner {
  private static final int ITERATIONS_COUNT = 5;
  private final BenchmarkRunner myRunner;

  public MultipleIterationsBenchmarkRunner(@NotNull BenchmarkRunner runner) {
    myRunner = runner;
  }

  public BenchmarkResult start() throws IOException {
    final InetAddress address = myRunner.getAddress();

    final int archIndex = Details.availableArchitectures().indexOf(myRunner.getArchitectureDescription());

    long minDuration = Long.MAX_VALUE;
    long minPerClientTime = Long.MAX_VALUE;
    long minPerRequestTime = Long.MAX_VALUE;
    for (int i = 0; i < ITERATIONS_COUNT; i++) {
      try (final Socket socket = new Socket(address, SingleServerStarter.PORT);
           final DataOutputStream os = new DataOutputStream(socket.getOutputStream());
           final DataInputStream is = new DataInputStream(socket.getInputStream())) {

        os.writeInt(archIndex);
        final int zero = is.readInt();
        assert 0 == zero;

        final long duration = myRunner.start();

        os.writeInt(0);

        final long requestDuration = is.readLong();
        final long clientDuration = is.readLong();
        minDuration = Math.min(minDuration, duration);
        minPerClientTime = Math.min(minPerClientTime, clientDuration);
        minPerRequestTime = Math.min(minPerRequestTime, requestDuration);
      } catch (BrokenBarrierException | InterruptedException e) {
        e.printStackTrace();
        return null;
      }
    }

    return new BenchmarkResult(minDuration, minPerClientTime, minPerRequestTime);
  }
}
