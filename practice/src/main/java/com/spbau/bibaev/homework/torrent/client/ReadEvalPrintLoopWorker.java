package com.spbau.bibaev.homework.torrent.client;

import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * @author Vitaliy.Bibaev
 */
public class ReadEvalPrintLoopWorker implements Runnable {
  private final InetAddress myServerAddress;
  private final int myServerPort;
  private final ClientStateEx myState;

  private boolean myIsCancelled = false;

  public ReadEvalPrintLoopWorker(@NotNull InetAddress serverAddress, @NotNull ClientStateEx state, int serverPort) {
    myServerAddress = serverAddress;
    myServerPort = serverPort;
    myState = state;
  }

  @Override
  public void run() {
    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    while (!myIsCancelled) {
      final String userInput;
      try {
        userInput = consoleReader.readLine();
        if (userInput.trim().isEmpty()) {
          continue;
        }

      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }
}
