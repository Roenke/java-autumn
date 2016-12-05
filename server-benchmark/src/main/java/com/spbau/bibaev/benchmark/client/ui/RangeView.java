package com.spbau.bibaev.benchmark.client.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * @author Vitaliy.Bibaev
 */
class RangeView extends JPanel {
  private final JTextField myFrom = new JTextField("1", 10);
  private final JTextField myTo = new JTextField("5", 10);
  private final JTextField myStep = new JTextField("1", 10);

  RangeView() {
    super(new BorderLayout());

    final JPanel textFieldsPane = new JPanel(new FlowLayout());
    textFieldsPane.add(new JLabel("from", SwingConstants.RIGHT));
    textFieldsPane.add(myFrom);
    textFieldsPane.add(new JLabel("to", SwingConstants.RIGHT));
    textFieldsPane.add(myTo);
    textFieldsPane.add(new JLabel("step", SwingConstants.RIGHT));
    textFieldsPane.add(myStep);

    add(new JLabel("Set the borders and step for selected parameter"), BorderLayout.NORTH);
    add(textFieldsPane, BorderLayout.CENTER);
  }

  @NotNull
  Iterator<Integer> getIterator() {
    final int from = Integer.parseInt(myFrom.getText());
    final int to = Integer.parseInt(myTo.getText());
    final int step = Integer.parseInt(myStep.getText());
    return new Iterator<Integer>() {
      private int myCurrent = from;

      @Override
      public boolean hasNext() {
        return myCurrent <= to;
      }

      @Override
      public Integer next() {
        int value = myCurrent;
        myCurrent += step;
        return value;
      }
    };
  }
}
