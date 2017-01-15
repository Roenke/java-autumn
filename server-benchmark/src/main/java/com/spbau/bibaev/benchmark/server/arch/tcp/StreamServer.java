package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Vitaliy.Bibaev
 */
abstract class StreamServer extends TcpServer {
  StreamServer(int port) {
    super(port);
  }

  void handle(@NotNull InputStream is, @NotNull OutputStream os) throws IOException {
    final long clientProcessingStart = System.nanoTime();
    final byte[] data = DataUtils.readData(is);

    final long requestProcessingStart = System.nanoTime();
    final int[] array = DataUtils.unbox(MessageProtos.Array.parseFrom(data));
    InsertionSorter.sort(array);
    final MessageProtos.Array message = DataUtils.toMessage(array);
    final long requestDuration = System.nanoTime() - requestProcessingStart;

    DataUtils.write(message, os);
    final long clientDuration = System.nanoTime() - clientProcessingStart;
    updateStatistics(clientDuration, requestDuration);
  }
}
