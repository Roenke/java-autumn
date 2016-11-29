package com.spbau.bibaev.benchmark.common;

/**
 * @author Vitaliy.Bibaev
 */
public interface Details {
  interface TcpPorts {
    int PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT = 10000;
    int PERMANENT_CONNECTION_CACHED_THREAD_POOL = 10001;
    int PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING = 10002;
    int NEW_CONNECTION_SINGLE_THREADED = 10003;
  }

  interface UdpPorts {
    int THREAD_PER_REQUEST = 10004;
    int FIXED_THREAD_POOL = 10005;
    int ASYNC_PROCESSING = 10005;
  }
}
