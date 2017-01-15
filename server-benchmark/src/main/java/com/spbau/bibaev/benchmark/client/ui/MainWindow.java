package com.spbau.bibaev.benchmark.client.ui;

import com.spbau.bibaev.benchmark.client.BenchmarkParameters;
import com.spbau.bibaev.benchmark.client.Log;
import com.spbau.bibaev.benchmark.client.runner.BenchmarkResult;
import com.spbau.bibaev.benchmark.client.runner.BenchmarkRunner;
import com.spbau.bibaev.benchmark.client.runner.MultipleIterationsBenchmarkRunner;
import com.spbau.bibaev.benchmark.common.ServerArchitectureDescription;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

/**
 * @author Vitaliy.Bibaev
 */
public class MainWindow extends JFrame {
  private final Log myLogPane;
  private final Log myFileLog;
  private final ConfigurationPanel myConfigurationPanel;
  private final JButton myRunButton = new JButton("Run");
  private final InetAddress myServerAddress;

  public MainWindow(@NotNull File logFile, @NotNull InetAddress address) throws HeadlessException {
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
    myFileLog = new Log() {
      @Override
      public void log(@NotNull String message) {
        try {
          FileUtils.write(logFile, message, "UTF-8", true);
        } catch (IOException e) {
          myLogPane.log("Cannot save log to file " + logFile.getName());
          e.printStackTrace();
        }
      }
    };

    myLogPane = log;
    myLogPane.log("Specify the parameters and press 'Run' to start benchmark");

    getContentPane().add(myRunButton, BorderLayout.SOUTH);
    setLocationRelativeTo(null);

    myRunButton.addActionListener(e -> {
      final ServerArchitectureDescription description = myConfigurationPanel.getServerArchitectureDescription();
      final Iterator<BenchmarkParameters> parameters = myConfigurationPanel.getParameters();
      if (parameters != null) {
        myLogPane.log("Evaluate...");

        myFileLog.log(String.format("New benchmark for %s! %n", description.getName()));
        myFileLog.log(String.format("%14s \t %14s \t %14s \t %14s \t %14s \t %14s \t %14s%n",
            "N", "M", "Delta", "X", "client", "server-client", "server-request"));
        new Thread(() -> {
          while (parameters.hasNext()) {
            final BenchmarkParameters params = parameters.next();
            final BenchmarkRunner runner = new BenchmarkRunner(params, myServerAddress, description);
            final MultipleIterationsBenchmarkRunner fewIterationsRunner = new MultipleIterationsBenchmarkRunner(runner);
            myLogPane.log(String.format("size = %d, clients = %d, delay = %d, iterations = %d",
                params.getDataSize(), params.getClientCount(), params.getDelay(), params.getIterationCount()));
            try {
              final BenchmarkResult result = fewIterationsRunner.start();
              myFileLog.log(String.format("%14d \t %14d \t %14d \t %14d \t %14d \t %14d \t %14d%n",
                  params.getDataSize(), params.getClientCount(), params.getDelay(), params.getIterationCount(),
                  result.averageClientLifeTime, result.averagePerClientTime, result.averagePerQueryTime));
              myLogPane.log(String.format("%d \t %d \t %d",
                  result.averageClientLifeTime, result.averagePerClientTime, result.averagePerQueryTime));
            } catch (Exception e1) {
              myLogPane.log("something went wrong :(" + e1.toString());
              return;
            }
          }
        }).start();
      } else {
        myLogPane.log("Check correctness of parameters");
      }
    });

    pack();
  }
}
