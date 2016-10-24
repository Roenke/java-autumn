package com.spbau.bibaev.practice.nonblocking.server;

import com.spbau.bibaev.practice.nonblocking.common.Common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class NioServer {

  private final InetSocketAddress inetSocketAddress;

  public static void main(String[] args) throws IOException {
    new NioServer(new InetSocketAddress(15638)).run();
  }

  public NioServer(InetSocketAddress inetSocketAddress) {
    this.inetSocketAddress = inetSocketAddress;
  }

  public void run() throws IOException {
    // TODO process exceptions

    final Selector selector = Selector.open();
    final ServerSocketChannel serverChannel = ServerSocketChannel.open();

    serverChannel.configureBlocking(false);
    serverChannel.bind(inetSocketAddress);
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    final ByteBuffer buffer = ByteBuffer.wrap(Files.readAllBytes(Paths.get(Common.RESOURCES_PATH + "java_logo.png")));

    while (true) {
      selector.select();

      final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

      while (iterator.hasNext()) {
        final SelectionKey key = iterator.next();
        iterator.remove();

        if (key.isAcceptable()) {
          final SocketChannel clientChannel = serverChannel.accept();

          clientChannel.configureBlocking(false);
          clientChannel.register(selector, SelectionKey.OP_WRITE);
        } else {
          final SocketChannel clientChannel = (SocketChannel) key.channel();

          while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
          }

          buffer.flip();

          clientChannel.close();
        }
      }
    }
  }
}