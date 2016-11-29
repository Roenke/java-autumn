package com.spbau.bibaev.benchmark.client.ui;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import java.util.function.Function;

/**
 * @author Vitaliy.Bibaev
 */
class ConfigurationPanel extends JPanel {
  private static final int DEFAULT_ELEMENT_COUNT = 100;
  private static final int DEFAULT_CLIENT_COUNT = 2;
  private static final int DEFAULT_DELAY_MS = 0;
  private static final int DEFAULT_QUERY_COUNT = 1;
  private static final int TEXT_EDITOR_COLUMN_COUNT = 15;

  private final JTextField myArraySizeTextField = new JTextField(
      String.valueOf(DEFAULT_ELEMENT_COUNT), TEXT_EDITOR_COLUMN_COUNT);

  private final JTextField myClientsCountTextField = new JTextField(
      String.valueOf(DEFAULT_CLIENT_COUNT), TEXT_EDITOR_COLUMN_COUNT);

  private final JTextField myDelayTextField = new JTextField(
      String.valueOf(DEFAULT_DELAY_MS), TEXT_EDITOR_COLUMN_COUNT);

  private final JTextField myIterationCountTextField = new JTextField(
      String.valueOf(DEFAULT_QUERY_COUNT), TEXT_EDITOR_COLUMN_COUNT);

  private boolean myIsValid = true;

  ConfigurationPanel() {
    super();

    addVerifier(myArraySizeTextField, false);
    addVerifier(myClientsCountTextField, false);
    addVerifier(myDelayTextField, true);
    addVerifier(myIterationCountTextField, false);

    JLabel delta = new JLabel("Interval between queries ms (Delta)");
    JLabel dataSize = new JLabel("Array size (N)");
    JLabel clientCount = new JLabel("Client Count (M)");
    JLabel iterationCount = new JLabel("Iteration Count (X)");

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(delta)
            .addComponent(dataSize)
            .addComponent(clientCount)
            .addComponent(iterationCount))
        .addGroup(layout.createParallelGroup()
            .addComponent(myDelayTextField)
            .addComponent(myArraySizeTextField)
            .addComponent(myClientsCountTextField)
            .addComponent(myIterationCountTextField)));

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(delta)
            .addComponent(myDelayTextField))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(dataSize)
            .addComponent(myArraySizeTextField))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(clientCount)
            .addComponent(myClientsCountTextField))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(iterationCount)
            .addComponent(myIterationCountTextField)));
  }

  private void addVerifier(@NotNull JTextField textField, boolean acceptZero) {
    final Function<Integer, Boolean> filter = acceptZero
        ? x -> x >= 0
        : x -> x > 0;
    textField.setInputVerifier(new MyNonNegativeIntegerInputValidator(filter));
    textField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        textField.getInputVerifier().shouldYieldFocus(textField);
      }
    });
  }

  @Nullable
  BenchmarkParameters getParameters() {
    assert SwingUtilities.isEventDispatchThread();
    if (!myIsValid) {
      return null;
    }

    final int dataSize = Integer.parseInt(myArraySizeTextField.getText());
    final int clients = Integer.parseInt(myClientsCountTextField.getText());
    final int iterations = Integer.parseInt(myIterationCountTextField.getText());
    final int delay = Integer.parseInt(myDelayTextField.getText());
    return new BenchmarkParameters(dataSize, clients, iterations, delay);
  }

  private boolean verifyAll() {
    return verify(myDelayTextField) && verify(myArraySizeTextField)
        && verify(myClientsCountTextField) && verify(myIterationCountTextField);
  }

  private boolean verify(@NotNull JTextField textField) {
    return textField.getInputVerifier().verify(textField);
  }

  private class MyNonNegativeIntegerInputValidator extends InputVerifier {
    final Function<Integer, Boolean> myFilter;

    MyNonNegativeIntegerInputValidator(@NotNull Function<Integer, Boolean> filter) {
      myFilter = filter;
    }

    @Override
    public boolean verify(JComponent input) {
      return input instanceof JTextField && isInteger(((JTextField) input).getText());

    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
      final boolean result = verify(input);
      final boolean before = myIsValid;
      if (result) {
        myIsValid = verifyAll();
        input.setForeground(Color.black);
      } else {
        myIsValid = false;
        input.setForeground(Color.red);
      }
      if (before != myIsValid) {
        firePropertyChange("validity", before, myIsValid);
      }
      return true;
    }

    private boolean isInteger(@NotNull String text) {
      Scanner scanner = new Scanner(text.trim());
      if (!scanner.hasNextInt(10)) {
        return false;
      }

      final int val = scanner.nextInt(10);
      return myFilter.apply(val) && !scanner.hasNextInt();
    }
  }
}
