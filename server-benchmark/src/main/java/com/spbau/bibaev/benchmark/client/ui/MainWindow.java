package com.spbau.bibaev.benchmark.client.ui;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import com.spbau.bibaev.benchmark.client.Log;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vitaliy.Bibaev
 */
public class MainWindow extends JFrame {
  private final Log myLog;
  private final ConfigurationPanel myConfigurationPanel;
  private final JButton myRunButton = new JButton("Run");

  public MainWindow() throws HeadlessException {
    super("Benchmark configuration");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    myConfigurationPanel = new ConfigurationPanel();
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(myConfigurationPanel, BorderLayout.NORTH);
    myConfigurationPanel.addPropertyChangeListener("validity",
        evt -> myRunButton.setEnabled((boolean) evt.getNewValue()));

    LogPanel log = new LogPanel();
    log.setPreferredSize(new Dimension(200, 350));
    getContentPane().add(new JScrollPane(log), BorderLayout.CENTER);
    myLog = log;
    myLog.log("Specify the parameters and press 'Run' to start benchmark");

    getContentPane().add(myRunButton, BorderLayout.SOUTH);
    setLocationRelativeTo(null);

    myRunButton.addActionListener(e -> {
      final BenchmarkParameters parameters = myConfigurationPanel.getParameters();
      if(parameters != null) {
        myLog.log("Evaluate...");
      } else {
        myLog.log("Check correctness of parameters");
      }
    });

    pack();
  }
}
