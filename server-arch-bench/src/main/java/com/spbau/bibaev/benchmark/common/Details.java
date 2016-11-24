package com.spbau.bibaev.benchmark.common;

/**
 * @author Vitaliy.Bibaev
 */
public class Details {
  public static final class TcpPorts {
    public static final int PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT = 10000;
    public static final int PERMANENT_CONNECTION_CACHED_THREAD_POOL = 10001;
    public static final int PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING = 10002;
    public static final int NEW_CONNECTION_SINGLE_THREADED = 10003;
  }

  public static final class UdpPorts {
    public static final int THREAD_PER_REQUEST = 10004;
    public static final int FIXED_THREAD_POOL = 10005;
    public static final int ASYNC_PROCESSING = 10005;
  }
}
