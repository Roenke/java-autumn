package com.spbau.bibaev.benchmark.common;

import java.io.*;

/**
 * @author Vitaliy.Bibaev
 */
public class DataUtils {
  public static int[] readArray(InputStream is) throws IOException {
    final int messageLength = new DataInputStream(is).readInt();
    byte[] data = new byte[messageLength];
    final int readCount = is.read(data);
    assert readCount == messageLength;

    final MessageProtos.Array array = MessageProtos.Array.parseFrom(data);
    return unbox(array);
  }

  public static void write(int[] array, OutputStream os) throws IOException {
    final MessageProtos.Array message = toMessage(array);
    new DataOutputStream(os).writeInt(message.getSerializedSize());
    os.write(message.toByteArray());
  }

  private static int[] unbox(MessageProtos.Array array) {
    int[] result = new int[array.getItemCount()];
    for (int i = 0; i < array.getItemCount(); i++) {
      result[i] = array.getItem(i);
    }

    return result;
  }

  private static MessageProtos.Array toMessage(int[] array) {
    final MessageProtos.Array.Builder builder = MessageProtos.Array.newBuilder();
    for (int val : array) {
      builder.addItem(val);
    }

    return builder.build();
  }
}
