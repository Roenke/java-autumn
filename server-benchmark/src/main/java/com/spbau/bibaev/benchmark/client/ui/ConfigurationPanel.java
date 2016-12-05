package com.spbau.bibaev.benchmark.client.ui;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import com.spbau.bibaev.benchmark.common.Details;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
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

  private static final String DELTA = "Interval between queries ms (Delta)";
  private static final String ARRAY_SIZE = "Array size (N)";
  private static final String CLIENT_COUNT = "Client Count (M)";
  private static final String ITERATION_COUNT = "Iteration Count (X)";

  private final JComboBox<ServerArchitectureDescription> myArchitectureComboBox;
  private final RangeView myRangeView = new RangeView();

  private final JTextField myDelayTextField = new JTextField(
      String.valueOf(DEFAULT_DELAY_MS), TEXT_EDITOR_COLUMN_COUNT);

  private final JTextField myArraySizeTextField = new JTextField(
      String.valueOf(DEFAULT_ELEMENT_COUNT), TEXT_EDITOR_COLUMN_COUNT);

  private final JTextField myClientCountTextField = new JTextField(
      String.valueOf(DEFAULT_CLIENT_COUNT), TEXT_EDITOR_COLUMN_COUNT);

  private final JTextField myIterationCountTextField = new JTextField(
      String.valueOf(DEFAULT_QUERY_COUNT), TEXT_EDITOR_COLUMN_COUNT);

  private boolean myIsValid = true;

  private final Map<String, JTextField> myAction2TextField = Collections.unmodifiableMap(
      new HashMap<String, JTextField>() {
        {
          put(DELTA, myDelayTextField);
          put(ARRAY_SIZE, myArraySizeTextField);
          put(CLIENT_COUNT, myClientCountTextField);
          put(ITERATION_COUNT, myIterationCountTextField);
        }
      });

  private volatile String mySelectedChangedParameter = null;

  ConfigurationPanel() {
    super(new BorderLayout());

    addVerifier(myArraySizeTextField, false);
    addVerifier(myClientCountTextField, false);
    addVerifier(myDelayTextField, true);
    addVerifier(myIterationCountTextField, false);

    ActionListener textFieldSwitcher = new MyRadioButtonActionHandler();
    JRadioButton delta = new JRadioButton("Interval between queries ms (Delta)");
    delta.addActionListener(textFieldSwitcher);

    JRadioButton dataSize = new JRadioButton("Array size (N)");
    dataSize.addActionListener(textFieldSwitcher);

    JRadioButton clientCount = new JRadioButton("Client Count (M)");
    clientCount.addActionListener(textFieldSwitcher);

    JLabel iterationCount = new JLabel("  Iteration Count (X)");
    iterationCount.setHorizontalTextPosition(SwingConstants.CENTER);

    ButtonGroup group = new ButtonGroup();
    group.add(delta);
    group.add(dataSize);
    group.add(clientCount);

    myArchitectureComboBox = new JComboBox<>(new Vector<>(Details.availableArchitectures()));
    myArchitectureComboBox.setRenderer(new MyArchitectureComboBoxCellRenderer());

    JPanel configPane = new JPanel();
    GroupLayout layout = new GroupLayout(configPane);
    configPane.setLayout(layout);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(iterationCount)
            .addComponent(delta)
            .addComponent(dataSize)
            .addComponent(clientCount))
        .addGroup(layout.createParallelGroup()
            .addComponent(myIterationCountTextField)
            .addComponent(myDelayTextField)
            .addComponent(myArraySizeTextField)
            .addComponent(myClientCountTextField)));

    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(iterationCount)
            .addComponent(myIterationCountTextField))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(delta)
            .addComponent(myDelayTextField))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(dataSize)
            .addComponent(myArraySizeTextField))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(clientCount)
            .addComponent(myClientCountTextField)));

    add(myArchitectureComboBox, BorderLayout.NORTH);
    add(configPane, BorderLayout.CENTER);
    add(myRangeView, BorderLayout.SOUTH);

    SwingUtilities.invokeLater(delta::doClick);
  }

  @NotNull
  ServerArchitectureDescription getServerArchitectureDescription() {
    return (ServerArchitectureDescription) myArchitectureComboBox.getSelectedItem();
  }

  @Nullable
  Iterator<BenchmarkParameters> getParameters() {
    assert SwingUtilities.isEventDispatchThread();
    if (!myIsValid) {
      return null;
    }

    final int x = Integer.parseInt(myIterationCountTextField.getText());

    final Iterator<Integer> changedValueIterator = myRangeView.getIterator();

    Iterator<Integer> dataSizeIterator = Objects.equals(mySelectedChangedParameter, ARRAY_SIZE)
        ? changedValueIterator :
        new ConstIterator(Integer.parseInt(myArraySizeTextField.getText()));

    final Iterator<Integer> clientCountIterator = Objects.equals(mySelectedChangedParameter, CLIENT_COUNT)
        ? changedValueIterator :
        new ConstIterator(Integer.parseInt(myClientCountTextField.getText()));

    final Iterator<Integer> delayIterator = Objects.equals(mySelectedChangedParameter, DELTA)
        ? changedValueIterator :
        new ConstIterator(Integer.parseInt(myDelayTextField.getText()));

    return BenchmarkParameters.createIterator(dataSizeIterator, clientCountIterator, delayIterator, x);
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

  private boolean verifyAll() {
    return verify(myDelayTextField) && verify(myArraySizeTextField)
        && verify(myClientCountTextField) && verify(myIterationCountTextField);
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

  private static class MyArchitectureComboBoxCellRenderer
      extends JLabel
      implements ListCellRenderer<ServerArchitectureDescription> {
    MyArchitectureComboBoxCellRenderer() {
      setOpaque(true);
      setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ServerArchitectureDescription> list,
                                                  ServerArchitectureDescription value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
      setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
      setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
      setText(value.getName());
      return this;
    }
  }

  private class MyRadioButtonActionHandler implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      final String command = e.getActionCommand();
      final JTextField textField = myAction2TextField.get(command);
      if (textField != null) {
        textField.setEnabled(false);
        if (mySelectedChangedParameter != null) {
          myAction2TextField.get(mySelectedChangedParameter).setEnabled(true);
        }

        mySelectedChangedParameter = command;
      }
    }
  }

  private static class ConstIterator implements Iterator<Integer> {
    private final Integer myValue;

    ConstIterator(int value) {
      myValue = value;
    }

    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public Integer next() {
      return myValue;
    }
  }
}
