package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitaliy.Bibaev
 */
public class AsyncServer extends TcpServer {
  private AsynchronousServerSocketChannel myChannel;

  public AsyncServer(int port) {
    super(port);
  }

  @Override
  void start() throws IOException {
    myChannel = AsynchronousServerSocketChannel.open();
    myChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1000);

    // read size -> read data -> sort -> write
    myChannel.bind(new InetSocketAddress(myPort)).accept(null, new MyAcceptHandler());
  }

  @Override
  void shutdown() throws IOException {
    myChannel.close();
  }

  private class MyAcceptHandler extends MyCompletionHandler<AsynchronousSocketChannel, Void> {
    @Override
    public void completed(AsynchronousSocketChannel channel, Void attachment) {
      myChannel.accept(null, this);
      ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
      channel.read(sizeBuffer, sizeBuffer, new MySizeReaderHandler(channel));
    }
  }

  private static class MySizeReaderHandler extends MyCompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel myChannel;

    MySizeReaderHandler(@NotNull AsynchronousSocketChannel channel) {
      myChannel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
      if (buffer.hasRemaining()) {
        myChannel.read(buffer, buffer, this);
        return;
      }

      buffer.flip();
      final int dataSize = buffer.getInt();
      final ByteBuffer dataBuffer = ByteBuffer.allocate(dataSize);
      myChannel.read(dataBuffer, dataBuffer, new MyDataReaderHandler(this, myChannel));
    }
  }

  private static class MyDataReaderHandler extends MyCompletionHandler<Integer, ByteBuffer> {
    private final MySizeReaderHandler myPreviousReader;
    private final AsynchronousSocketChannel myChannel;

    MyDataReaderHandler(@NotNull MySizeReaderHandler sizeReader, @NotNull AsynchronousSocketChannel channel) {
      myPreviousReader = sizeReader;
      myChannel = channel;
    }

    @Override
    public void completed(@NotNull Integer result, @NotNull ByteBuffer dataBuffer) {
      if (dataBuffer.hasRemaining()) {
        myChannel.read(dataBuffer, dataBuffer, this);
        return;
      }

      dataBuffer.flip();
      try {
        int[] array = DataUtils.unbox(MessageProtos.Array.parseFrom(dataBuffer.array()));
        InsertionSorter.sort(array);
        MessageProtos.Array message = DataUtils.toMessage(array);

        ByteBuffer size = ByteBuffer.allocate(4);
        size.putInt(message.getSerializedSize());
        size.flip();
        ByteBuffer buffer = ByteBuffer.wrap(message.toByteArray());
        ByteBuffer[] data = {size, buffer};
        myChannel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, data,
            new MyResultWriterHandler(myChannel));
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }
    }
  }

  private static class MyResultWriterHandler extends MyCompletionHandler<Long, ByteBuffer[]> {
    private final AsynchronousSocketChannel myChannel;

    MyResultWriterHandler(@NotNull AsynchronousSocketChannel channel) {
      myChannel = channel;
    }

    @Override
    public void completed(@NotNull Long result, @NotNull ByteBuffer[] data) {
      if (data[1].hasRemaining()) {
        myChannel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, data, this);
      }

      // All data sent. Start to listen to a next message
      ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
      myChannel.read(sizeBuffer, sizeBuffer, new MySizeReaderHandler(myChannel));
    }
  }

  private static abstract class MyCompletionHandler<V, A> implements CompletionHandler<V, A> {
    @Override
    public void failed(Throwable exc, A attachment) {
      // ignore
    }
  }
}
