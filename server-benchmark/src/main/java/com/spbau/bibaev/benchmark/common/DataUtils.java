package com.spbau.bibaev.benchmark.common;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 * @author Vitaliy.Bibaev
 */
public class DataUtils {
  public static byte[] readData(@NotNull InputStream is) throws IOException {
    final int messageLength = new DataInputStream(is).readInt();
    byte[] data = new byte[messageLength];
    final int readCount = IOUtils.read(is, data);
    assert readCount == messageLength;

    return data;
  }

  public static void write(@NotNull MessageProtos.Array message, @NotNull OutputStream os) throws IOException {
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
    return unbox(readToArray(packet));
  }

  public static MessageProtos.Array readToArray(@NotNull DatagramPacket packet) throws InvalidProtocolBufferException {
    final ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
    final int length = buffer.getInt();

    byte[] content = new byte[length];
    buffer.get(content);
    return MessageProtos.Array.parseFrom(content);
  }

  public static void write(@NotNull int[] array, @NotNull DatagramPacket packet, @NotNull byte[] buffer) {
    final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    final MessageProtos.Array message = toMessage(array);
    byteBuffer.putInt(message.getSerializedSize());
    byteBuffer.put(message.toByteArray());
    packet.setData(byteBuffer.array(), 0, byteBuffer.position());
  }

  @NotNull
  public static DatagramPacket createPacket(@NotNull int[] array) {
    final MessageProtos.Array message = toMessage(array);

    final byte[] bytes = new byte[message.getSerializedSize() + 4];
    final DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
    final ByteBuffer wrapper = ByteBuffer.wrap(bytes);

    wrapper.putInt(message.getSerializedSize());
    wrapper.put(message.toByteArray());
    packet.setData(wrapper.array());

    return packet;
  }
}
