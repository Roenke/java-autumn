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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class NonblockingServer extends TcpServer {
  private static final int THREAD_POOL_SIZE = 4;

  private Selector mySelector;
  private final ExecutorService myThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  private volatile ServerSocketChannel mySocketChannel;
  private final List<SocketChannel> myActiveChannels = new CopyOnWriteArrayList<>();

  public NonblockingServer(int port) {
    super(port);
  }

  @Override
  void start() {
    try {
      mySelector = Selector.open();

      mySocketChannel = ServerSocketChannel.open();
      mySocketChannel.bind(new InetSocketAddress(myPort));
      mySocketChannel.configureBlocking(false);

      mySocketChannel.register(mySelector, SelectionKey.OP_ACCEPT);
      while (mySocketChannel.isOpen()) {
        mySelector.select();
        Iterator<SelectionKey> keyIterator = mySelector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();

          if (key.isAcceptable()) {
            accept(key);
          }

          if (key.isReadable()) {
            read(key);
          }

          if (key.isWritable()) {
            write(key);
          }

          keyIterator.remove();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read(@NotNull SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    checkContext(key);

    final ChannelContext context = (ChannelContext) key.attachment();
    switch (context.state) {
      case READ_SIZE:
        channel.read(context.sizeBuffer);
        if (context.sizeBuffer.hasRemaining()) {
          break;
        }

        context.sizeBuffer.flip();
        int size = context.sizeBuffer.getInt();
        context.dataBuffer = ByteBuffer.allocate(size);
        context.state = ChannelState.READ_DATA;
      case READ_DATA:
        channel.read(context.dataBuffer);
        if (context.dataBuffer.hasRemaining()) {
          break;
        }
        context.state = ChannelState.PROCEED;
        myThreadPool.execute(() -> {
          context.requestHandlingStart = System.nanoTime();
          byte[] data = (byte[]) context.dataBuffer.flip().array();
          try {
            final MessageProtos.Array message = MessageProtos.Array.parseFrom(data);
            final int[] array = DataUtils.unbox(message);
            InsertionSorter.sort(array);
            final byte[] answer = DataUtils.toMessage(array).toByteArray();
            final ByteBuffer sizeBuffer = ByteBuffer.allocate(4).putInt(answer.length);
            sizeBuffer.flip();
            context.answer = new ByteBuffer[]{sizeBuffer, ByteBuffer.wrap(answer)};
            context.clientHandlingDuration = System.nanoTime() - context.requestHandlingStart;
            context.state = ChannelState.WRITE;
          } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
          }
        });
      default:
        break;
    }
  }

  private void checkContext(@NotNull SelectionKey key) {
    final ChannelContext context = (ChannelContext) key.attachment();
    if (context == null || context.state == ChannelState.DONE) {
      final ChannelContext newContext = new ChannelContext();
      newContext.clientHandlingStart = System.nanoTime();
      key.attach(newContext);
    }
  }

  private void write(@NotNull SelectionKey key) throws IOException {
    final ChannelContext context = (ChannelContext) key.attachment();
    if (context.state == ChannelState.WRITE) {
      SocketChannel channel = (SocketChannel) key.channel();
      if (context.answer[1].hasRemaining()) {
        channel.write(context.answer);
      }

      if (!context.answer[1].hasRemaining()) {
        context.clientHandlingDuration = System.nanoTime() - context.clientHandlingStart;
        updateStatistics(context.clientHandlingDuration, context.requestHandlingDuration);
        context.state = ChannelState.DONE;
      }
    }
  }

  private void accept(@NotNull SelectionKey key) throws IOException {
    final SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
    channel.configureBlocking(false);
    channel.socket().setTcpNoDelay(true);
    channel.register(mySelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
        new ChannelContext());
    myActiveChannels.add(channel);
  }

  @Override
  public void shutdown() throws IOException {
    if (mySocketChannel != null) {
      mySocketChannel.close();
      mySocketChannel.socket().close();
    }

    for (SocketChannel channel : myActiveChannels) {
      channel.close();
    }
  }

  private static class ChannelContext {
    volatile long requestHandlingStart;
    volatile long clientHandlingStart;
    volatile long requestHandlingDuration;
    volatile long clientHandlingDuration;
    final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    ByteBuffer dataBuffer;
    volatile ByteBuffer[] answer;
    volatile ChannelState state = ChannelState.READ_SIZE;

    ChannelContext() {
      super();
    }
  }

  private enum ChannelState {
    READ_SIZE, READ_DATA, PROCEED, WRITE, DONE
  }
}
