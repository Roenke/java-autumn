package com.spbau.bibaev.benchmark.client.ui;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import com.spbau.bibaev.benchmark.client.Log;
import com.spbau.bibaev.benchmark.client.runner.BenchmarkResult;
import com.spbau.bibaev.benchmark.client.runner.BenchmarkRunner;
import com.spbau.bibaev.benchmark.client.runner.MultipleIterationsBenchmarkRunner;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.Iterator;

/**
 * @author Vitaliy.Bibaev
 */
public class MainWindow extends JFrame {
  private final Log myLog;
  private final ConfigurationPanel myConfigurationPanel;
  private final JButton myRunButton = new JButton("Run");
  private final InetAddress myServerAddress;

  public MainWindow(InetAddress address) throws HeadlessException {
    super("Benchmark configuration");

    myServerAddress = address;

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
      final ServerArchitectureDescription description = myConfigurationPanel.getServerArchitectureDescription();
      final Iterator<BenchmarkParameters> parameters = myConfigurationPanel.getParameters();
      if (parameters != null) {
        myLog.log("Evaluate...");
        new Thread(() -> {
          while (parameters.hasNext()) {
            final BenchmarkParameters params = parameters.next();
            final BenchmarkRunner runner = new BenchmarkRunner(params, myServerAddress, description);
            final MultipleIterationsBenchmarkRunner fewIterationsRunner = new MultipleIterationsBenchmarkRunner(runner);
            myLog.log(String.format("size = %d, clients = %d, delay = %d, iterations = %d",
                params.getDataSize(), params.getClientCount(), params.getDelay(), params.getIterationCount()));
            try {
              final BenchmarkResult result = fewIterationsRunner.start();
              myLog.log(String.format("%d \t %d \t %d",
                  result.averageClientLifeTime, result.averagePerClientTime, result.averagePerQueryTime));
            } catch (Exception e1) {
              myLog.log("something went wrong :(" + e1.toString());
              return;
            }
          }
        }).start();
      } else {
        myLog.log("Check correctness of parameters");
      }
    });

    pack();
  }
}
