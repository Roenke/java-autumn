package com.spbau.bibaev.benchmark.client;

import com.spbau.bibaev.benchmark.client.ui.MainWindow;

import java.io.IOException;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientEntryPoint {
  public static void main(String[] args) throws IOException {
    final MainWindow mainWindow = new MainWindow();
    mainWindow.setVisible(true);
  }
}
