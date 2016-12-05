package com.spbau.bibaev.benchmark.server.arch.tcp;

import com.spbau.bibaev.benchmark.common.DataUtils;
import com.spbau.bibaev.benchmark.server.sorting.InsertionSorter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Vitaliy.Bibaev
 */
class ConnectionHandler implements Runnable {
  private final Socket mySocket;

  ConnectionHandler(Socket socket) {
    mySocket = socket;
  }

  @Override
  public void run() {
    try (InputStream is = mySocket.getInputStream(); OutputStream os = mySocket.getOutputStream()) {
      while (!mySocket.isClosed()) {
        int[] array = DataUtils.readArray(is);
        InsertionSorter.sort(array);
        DataUtils.write(array, os);
      }
    } catch (EOFException e) {
      // usual case - client just disconnected. Do nothing
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
