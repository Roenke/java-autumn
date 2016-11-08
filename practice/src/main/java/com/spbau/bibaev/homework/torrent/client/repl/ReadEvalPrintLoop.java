package com.spbau.bibaev.homework.torrent.client.repl;

import com.spbau.bibaev.homework.torrent.client.download.DownloadManager;
import com.spbau.bibaev.homework.torrent.client.ExitListener;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.repl.command.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Vitaliy.Bibaev
 */
public class ReadEvalPrintLoop implements Runnable {
  private final ClientStateEx myState;
  private final List<ExitListener> myExitListeners = new CopyOnWriteArrayList<>();

  private boolean myIsCancelled = false;

  private final Map<String, UserCommand> USER_COMMANDS;

  public ReadEvalPrintLoop(@NotNull InetAddress serverAddress, int serverPort, @NotNull ClientStateEx state,
                           @NotNull DownloadManager downloader) {
    myState = state;
    Map<String, UserCommand> commands = new HashMap<>();

    commands.put("help", new MyHelpCommand());
    commands.put("exit", new MyExitCommand());
    commands.put("download", new DownloadCommand(downloader));
    commands.put("list", new ListCommand(serverAddress, serverPort));
    commands.put("local", new LocalCommand());
    commands.put("progress", new ProgressCommand());
    commands.put("upload", new UploadCommand(serverAddress, serverPort));

    USER_COMMANDS = Collections.unmodifiableMap(commands);
  }

  @Override
  public void run() {
    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    UserCommand unknownCommand = new MyUnknownCommand();
    while (!myIsCancelled) {
      final String userInput;
      try {
        userInput = consoleReader.readLine();
        if (userInput.trim().isEmpty()) {
          continue;
        }

        final String[] args = userInput.split("\\s+");
        UserCommand command = USER_COMMANDS.getOrDefault(args[0], unknownCommand);
        command.execute(myState, args);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  public void addExitListener(@NotNull ExitListener listener) {
    myExitListeners.add(listener);
  }

  private class MyHelpCommand implements UserCommand {
    @Override
    public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
      System.out.println("The torrent client REPL. ");
      System.out.println("You can use any of the following commands:");
      for (String name : USER_COMMANDS.keySet()) {
        UserCommand command = USER_COMMANDS.get(name);
        System.out.println("\t" + name + " - " + command.getDescription());
        System.out.println("\tUsage: " + command.getUsage());
        System.out.println();
      }
    }

    @Override
    public String getUsage() {
      return "help";
    }

    @Override
    public String getDescription() {
      return "Prints list of available commands";
    }
  }

  private class MyExitCommand implements UserCommand {
    @Override
    public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
      myIsCancelled = true;
      myExitListeners.forEach(ExitListener::onExit);
    }

    @Override
    public String getUsage() {
      return "exit";
    }

    @Override
    public String getDescription() {
      return "Shutdown the application";
    }
  }

  private static class MyUnknownCommand implements UserCommand {

    @Override
    public void execute(@NotNull ClientStateEx state, @NotNull String[] args) {
      String name = args[0];
      System.out.println("Command \"" + name + "\" not found.");
    }

    @Override
    public String getUsage() {
      return "";
    }

    @Override
    public String getDescription() {
      return "";
    }
  }
}
