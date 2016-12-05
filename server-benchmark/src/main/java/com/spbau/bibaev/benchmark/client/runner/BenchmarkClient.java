package com.spbau.bibaev.benchmark.client.runner;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import com.spbau.bibaev.benchmark.common.Protocol;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class BenchmarkClient {

  public static BenchmarkClient create(@NotNull BenchmarkParameters parameters,
                                       @NotNull ServerArchitectureDescription description,
                                       @NotNull InetAddress address) {
    if (description.getProtocol() == Protocol.TCP) {
      return new TcpBenchmarkClient(parameters.getDataSize(), parameters.getDelay(),
          parameters.getIterationCount(), address,
          description.getServerPort(), description.holdConnection());
    } else {
      return new UdpBenchmarkClient(parameters.getDataSize(), parameters.getDelay(),
          parameters.getIterationCount(), address,
          description.getServerPort());
    }
  }

  public abstract void start() throws Exception;
}
