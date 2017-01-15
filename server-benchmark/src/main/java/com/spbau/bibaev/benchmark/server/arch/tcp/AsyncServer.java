package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    // attach -> read size -> read data -> sort -> write -> read size -> ...
    myChannel.bind(new InetSocketAddress(myPort), Integer.MAX_VALUE)
        .accept(null, new MyAcceptHandler());
  }

  @Override
  public void shutdown() throws IOException {
    myChannel.close();
  }

  private class MyAcceptHandler extends MyCompletionHandler<AsynchronousSocketChannel, Void> {
    @Override
    public void completed(@NotNull AsynchronousSocketChannel channel, Void attachment) {
      myChannel.accept(null, this);
      ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
      channel.read(sizeBuffer, sizeBuffer, new MySizeReaderHandler(channel));
    }
  }

  private class MySizeReaderHandler extends MyCompletionHandler<Integer, ByteBuffer> {
    private final AsynchronousSocketChannel myChannel;
    private volatile long myClientHandlingStartTime;

    MySizeReaderHandler(@NotNull AsynchronousSocketChannel channel) {
      myChannel = channel;
      myClientHandlingStartTime = -1;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
      if (result == -1) {
        return;
      }

      if (myClientHandlingStartTime < 0 && result > 0) {
        myClientHandlingStartTime = System.nanoTime();
      }

      if (buffer.hasRemaining()) {
        if (myChannel.isOpen()) {
          myChannel.read(buffer, buffer, this);
        }

        return;
      }

      buffer.flip();
      final int dataSize = buffer.getInt();
      final ByteBuffer dataBuffer = ByteBuffer.allocate(dataSize);
      myChannel.read(dataBuffer, dataBuffer, new MyDataReaderHandler(myClientHandlingStartTime, myChannel));
    }
  }

  private class MyDataReaderHandler extends MyCompletionHandler<Integer, ByteBuffer> {
    private final AsynchronousSocketChannel myChannel;
    private final long myClientHandlingStartTime;

    MyDataReaderHandler(long clientHandlingStartTime, @NotNull AsynchronousSocketChannel channel) {
      myChannel = channel;
      myClientHandlingStartTime = clientHandlingStartTime;
    }

    @Override
    public void completed(@NotNull Integer result, @NotNull ByteBuffer dataBuffer) {
      if (result == -1) {
        return;
      }

      if (dataBuffer.hasRemaining()) {
        myChannel.read(dataBuffer, dataBuffer, this);
        return;
      }

      dataBuffer.flip();
      try {
        final long requestHandlingStartTime = System.nanoTime();
        int[] array = DataUtils.unbox(MessageProtos.Array.parseFrom(dataBuffer.array()));
        InsertionSorter.sort(array);
        MessageProtos.Array message = DataUtils.toMessage(array);

        ByteBuffer size = ByteBuffer.allocate(4);
        size.putInt(message.getSerializedSize());
        size.flip();
        ByteBuffer buffer = ByteBuffer.wrap(message.toByteArray());
        ByteBuffer[] data = {size, buffer};
        final long requestHandlingTimeDuration = System.nanoTime() - requestHandlingStartTime;
        myChannel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, data,
            new MyResultWriterHandler(requestHandlingTimeDuration, myClientHandlingStartTime, myChannel));
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }
    }
  }

  private class MyResultWriterHandler extends MyCompletionHandler<Long, ByteBuffer[]> {
    private final AsynchronousSocketChannel myChannel;
    private final long myClientHandlingStartTime;
    private final long myRequestHandlingDuration;

    MyResultWriterHandler(long requestHandlingDuration, long clientHandlingStartTime, @NotNull AsynchronousSocketChannel channel) {
      myChannel = channel;
      myClientHandlingStartTime = clientHandlingStartTime;
      myRequestHandlingDuration = requestHandlingDuration;
    }

    @Override
    public void completed(@NotNull Long result, @NotNull ByteBuffer[] data) {
      if (data[1].hasRemaining()) {
        myChannel.write(data, 0, 2, 0, TimeUnit.NANOSECONDS, data, this);
        return;
      }

      final long clientHandlingDuration = System.nanoTime() - myClientHandlingStartTime;
      updateStatistics(clientHandlingDuration, myRequestHandlingDuration);

      // All data sent. Start to listen to a next message
      ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
      myChannel.read(sizeBuffer, sizeBuffer, new MySizeReaderHandler(myChannel));
    }
  }

  private static abstract class MyCompletionHandler<V, A> implements CompletionHandler<V, A> {
    @Override
    public void failed(Throwable exc, A attachment) {
      exc.printStackTrace();
    }
  }
}
