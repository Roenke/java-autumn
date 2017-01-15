package com.spbau.bibaev.benchmark.client.ui;

import com.spbau.bibaev.benchmark.client.Log;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vitaliy.Bibaev
 */
public class LogPanel extends JPanel implements Log {

  private final JTextArea myTextArea = new JTextArea();

  LogPanel() {
    super(new BorderLayout());
    add(new JScrollPane(myTextArea));
    myTextArea.setEditable(false);
  }

  @Override
  public void log(@NotNull String message) {
    if (SwingUtilities.isEventDispatchThread()) {
      addMessage(message);
    } else {
      SwingUtilities.invokeLater(() -> addMessage(message));
    }
  }

  private void addMessage(@NotNull String text) {
    myTextArea.append(text + System.lineSeparator());
  }
}
