package com.spbau.bibaev.benchmark.client;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientEntryPoint {
  public static void main(String[] args) throws IOException {
    final Socket socket = new Socket(InetAddress.getLocalHost(), Details.TcpPorts.PERMANENT_CONNECTION_NEW_THREAD_PER_CLIENT);
    int[] arr = new int[]{3, 1, 2, 4, 5, 6, 7, 4, 32, 4, 32, 4456, 456, 54, 67, 34, 4532, 45, 34, 5645, 6, 4, 5, 324, 5, 34, 5, 345, 5, 46, 4, 56, 45, 6, 455, 34, 5, 34, 5, 34};
    DataUtils.write(arr, socket.getOutputStream());
    final int[] result = DataUtils.readArray(socket.getInputStream());
    System.out.println(Arrays.toString(result));
  }
}
