package com.spbau.bibaev.practice.bot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Server {
  private static final int BUFFER_SIZE = 4096;

  public static void main(String[] args) throws IOException {

    AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open();
    channel.bind(new InetSocketAddress("localhost", 9123));

    channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
      @Override
      public void completed(AsynchronousSocketChannel ch, Void attachment) {
        channel.accept(null, this);


        ch.read(ByteBuffer.allocate(BUFFER_SIZE), null, new CompletionHandler<Integer, Object>() {
          @Override
          public void completed(Integer result, Object attachment) {
            if (result == -1) {
              System.err.println("no data received");
            } else {

            }
          }

          @Override
          public void failed(Throwable exc, Object attachment) {
            System.err.println("Client handling failed");
          }
        });
      }

      @Override
      public void failed(Throwable exc, Void attachment) {
        System.out.println("Something went wrong :(");
        System.out.println(exc.toString());
      }
    });
  }
}
