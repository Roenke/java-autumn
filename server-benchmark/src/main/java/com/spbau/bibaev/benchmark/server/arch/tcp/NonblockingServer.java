package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.MessageProtos;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitaliy.Bibaev
 */
public class NonblockingServer implements Runnable {
  private static final int PORT = Details.TcpPorts.PERMANENT_CONNECTION_FIXED_POOL_NONBLOCKING;
  private static final int THREAD_POOL_SIZE = 4;

  private final ExecutorService myThreadPool = Executors.newFixedThreadPool(4);

  @Override
  public void run() {
    try {
      ServerSocketChannel socketChannel = ServerSocketChannel.open();
      socketChannel.bind(new InetSocketAddress("localhost", PORT));
      socketChannel.configureBlocking(false);

      Selector acceptSelector = Selector.open();
      Selector ioSelector = Selector.open();

      socketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
      while (true) {
        Iterator<SelectionKey> acceptKeyIterator = acceptSelector.selectedKeys().iterator();
        while (acceptKeyIterator.hasNext()) {
          SelectionKey key = acceptKeyIterator.next();

          if (key.isAcceptable()) {
            final SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
            channel.configureBlocking(false);
            channel.socket().setTcpNoDelay(true);
            socketChannel.register(ioSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new ChannelContext());
          }
          acceptKeyIterator.remove();
        }

        Iterator<SelectionKey> ioKeyIterator = ioSelector.selectedKeys().iterator();
        while (ioKeyIterator.hasNext()) {
          SelectionKey key = ioKeyIterator.next();
          ChannelContext context = (ChannelContext) key.attachment();

          if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            switch (context.state) {
              case READ_SIZE:
                channel.read(context.sizeBuffer);
                if (context.sizeBuffer.hasRemaining()) {
                  break;
                }

                ((ByteBuffer) context.sizeBuffer.flip()).getInt();
                int size = context.sizeBuffer.getInt();
                context.dataBuffer = ByteBuffer.allocate(size);
                context.state = ChannelState.READ_DATA;
              case READ_DATA:
                channel.read(context.dataBuffer);
                if (context.dataBuffer.hasRemaining()) {
                  break;
                }
                context.state = ChannelState.RPOCEED;
                myThreadPool.execute(() -> {
                  byte[] data = (byte[]) context.dataBuffer.flip().array();
                  try {
                    final MessageProtos.Array message = MessageProtos.Array.parseFrom(data);
                    final int[] array = DataUtils.unbox(message);
                    InsertionSorter.sort(array);
                    final byte[] answer = DataUtils.toMessage(array).toByteArray();
                    context.answer = new ByteBuffer[]{ByteBuffer.allocate(4).putInt(answer.length), ByteBuffer.wrap(answer)};
                    context.state = ChannelState.WRITE;
                  } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                  }
                });
              case RPOCEED:
                break;
              case WRITE:
                channel.write(context.answer);
                if (context.answer[1].hasRemaining()) {
                  break;
                }
            }
          }

          if (key.isWritable()) {

          }

          ioKeyIterator.remove();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class ChannelContext {
    Object answerLock = new Object();
    final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    ByteBuffer dataBuffer;
    volatile ByteBuffer[] answer;
    volatile ChannelState state = ChannelState.READ_SIZE;

    ChannelContext() {
      super();
    }
  }

  enum ChannelState {
    READ_SIZE, READ_DATA, RPOCEED, WRITE
  }
}
