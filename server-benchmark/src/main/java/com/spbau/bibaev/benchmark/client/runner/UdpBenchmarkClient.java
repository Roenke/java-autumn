package com.spbau.bibaev.benchmark.client.runner;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import org.jetbrains.annotations.NotNull;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy.Bibaev
 */
public class UdpBenchmarkClient extends BenchmarkClient {
  private static final int MAX_DATAGRAM_LENGTH = 65000;
  private final int[] myData;
  private final int myDelay;
  private final int myIterationCount;
  private final InetAddress myServerAddress;
  private final int myServerPort;

  UdpBenchmarkClient(int dataSize, int delayMs, int iterationCount,
                     @NotNull InetAddress serverAddress, int serverPort) {
    myData = new Random().ints().limit(dataSize).toArray();
    myDelay = delayMs;
    myIterationCount = iterationCount;
    myServerAddress = serverAddress;
    myServerPort = serverPort;
  }

  @Override
  public void start() throws Exception {
    final MessageProtos.Array message = DataUtils.toMessage(myData);
    ByteBuffer wrapper = ByteBuffer.allocate(message.getSerializedSize() + 4);
    wrapper.putInt(message.getSerializedSize());
    wrapper.put(message.toByteArray());
    byte[] data = wrapper.array();

    if (data.length > MAX_DATAGRAM_LENGTH) {
      throw new Exception("Maximum UDP data length exceeded");
    }

    byte[] buffer = new byte[1 << 16]; // 64 kb

    for (int i = 0; i < myIterationCount; i++) {
      final DatagramSocket socket = new DatagramSocket();

      final DatagramPacket datagram = new DatagramPacket(data, data.length, myServerAddress, myServerPort);
      socket.send(datagram);

      DatagramPacket result = new DatagramPacket(buffer, buffer.length);
      socket.receive(result);
      ByteBuffer resultWrapper = ByteBuffer.wrap(result.getData());
      int size = resultWrapper.getInt();
      byte[] answer = new byte[size];
      resultWrapper.get(answer);
      final MessageProtos.Array array = MessageProtos.Array.parseFrom(answer);
      System.err.println(array.getItem(array.getItemCount() - 1));

      TimeUnit.MILLISECONDS.sleep(myDelay);
    }
  }
}
