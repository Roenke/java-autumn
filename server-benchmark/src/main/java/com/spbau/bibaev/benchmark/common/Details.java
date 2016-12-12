package com.spbau.bibaev.benchmark.common;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class Details {
  public interface TcpPorts {
    int PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT = 10000;
    int PERMANENT_CONNECTION_CACHED_THREAD_POOL = 10001;
    int PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING = 10002;
    int NEW_CONNECTION_SINGLE_THREADED = 10003;
    int ASYNC_PROCESSING = 10005;
  }

  public interface UdpPorts {
    int THREAD_PER_REQUEST = 10006;
    int FIXED_THREAD_POOL = 10007;
  }

  private static final List<ServerArchitectureDescription> AVAILABLE_ARCHITECTURES;

  static {
    final List<ServerArchitectureDescription> architectures = Arrays.asList(
        new ServerArchitectureDescriptionImpl("TCP: Permanent connection with separate threads",
            TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT, true, Protocol.TCP),
        new ServerArchitectureDescriptionImpl("TCP: Permanent connection with cached thread pool",
            TcpPorts.PERMANENT_CONNECTION_CACHED_THREAD_POOL, true, Protocol.TCP),
        new ServerArchitectureDescriptionImpl("TCP: Nonblocking permanent connection",
            TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING, true, Protocol.TCP),
        new ServerArchitectureDescriptionImpl("TCP: New connection with single threaded processing",
            TcpPorts.NEW_CONNECTION_SINGLE_THREADED, false, Protocol.TCP),
        new ServerArchitectureDescriptionImpl("TCP: Async mode",
            TcpPorts.ASYNC_PROCESSING, false, Protocol.TCP),
        new ServerArchitectureDescriptionImpl("UDP: Several thread per request",
            UdpPorts.THREAD_PER_REQUEST, false, Protocol.UDP),
        new ServerArchitectureDescriptionImpl("UDP: Fixed thread pool for request processing",
            UdpPorts.FIXED_THREAD_POOL, false, Protocol.UDP)
    );
    AVAILABLE_ARCHITECTURES = Collections.unmodifiableList(architectures);
  }

  public static List<ServerArchitectureDescription> availableArchitectures() {
    return Collections.unmodifiableList(AVAILABLE_ARCHITECTURES);
  }

  private static class ServerArchitectureDescriptionImpl implements ServerArchitectureDescription {
    private final String myName;
    private final int myPort;
    private final boolean myHoldConnection;
    private final Protocol myProtocol;

    private ServerArchitectureDescriptionImpl(@NotNull String name, int port, boolean holdConnection, @NotNull Protocol protocol) {
      myName = name;
      myPort = port;
      myHoldConnection = holdConnection;
      myProtocol = protocol;
    }

    @NotNull
    @Override
    public String getName() {
      return myName;
    }

    @NotNull
    @Override
    public Protocol getProtocol() {
      return myProtocol;
    }

    @Override
    public int getDefaultServerPort() {
      return myPort;
    }

    @Override
    public boolean holdConnection() {
      return myHoldConnection;
    }
  }
}


