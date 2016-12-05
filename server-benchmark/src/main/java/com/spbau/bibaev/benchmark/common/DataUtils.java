package com.spbau.bibaev.benchmark.common;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 * @author Vitaliy.Bibaev
 */
public class DataUtils {
  public static int[] readArray(@NotNull InputStream is) throws IOException {
    final int messageLength = new DataInputStream(is).readInt();
    byte[] data = new byte[messageLength];
    final int readCount = IOUtils.read(is, data);
    assert readCount == messageLength;

    final MessageProtos.Array array = MessageProtos.Array.parseFrom(data);
    return unbox(array);
  }

  public static void write(@NotNull int[] array, @NotNull OutputStream os) throws IOException {
    final MessageProtos.Array message = toMessage(array);
    new DataOutputStream(os).writeInt(message.getSerializedSize());
    os.write(message.toByteArray());
  }

  public static int[] unbox(@NotNull MessageProtos.Array array) {
    int[] result = new int[array.getItemCount()];
    for (int i = 0; i < array.getItemCount(); i++) {
      result[i] = array.getItem(i);
    }

    return result;
  }

  public static MessageProtos.Array toMessage(@NotNull int[] array) {
    final MessageProtos.Array.Builder builder = MessageProtos.Array.newBuilder();
    for (int val : array) {
      builder.addItem(val);
    }

    return builder.build();
  }

  public static int[] read(@NotNull DatagramPacket packet) throws IOException {
    final ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
    final int length = buffer.getInt();

    byte[] content = new byte[length];
    buffer.get(content);

    return unbox(MessageProtos.Array.parseFrom(content));
  }

  public static void write(@NotNull int[] array, @NotNull DatagramPacket packet, @NotNull byte[] buffer) {
    final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    final MessageProtos.Array message = toMessage(array);
    byteBuffer.putInt(message.getSerializedSize());
    byteBuffer.put(message.toByteArray());
    packet.setData(byteBuffer.array(), 0, byteBuffer.position());
  }
}
