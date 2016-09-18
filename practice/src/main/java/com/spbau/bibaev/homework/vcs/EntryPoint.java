package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supporting commands:
 * init
 * user [name]
 * branch name
 * commit [message]
 * log
 * TODO: status
 * TODO: checkout branch_name|revision_hash
 * TODO: merge [branch]
 */
public class EntryPoint {
  public static String VCS_NAME = "my_vcs";
  public static void main(String[] args) {
    if (args.length == 0) {
      usage();
      return;
    }

    File currentDirectory = new File(System.getProperty("user.dir"));
    Command command = CommandFactory.createCommand(currentDirectory, args[0]);
    if (command == null) {
      usage();
      return;
    }

    List<String> commandArgs = Arrays.stream(args).skip(1).collect(Collectors.toList());
    command.perform(commandArgs);
  }

  private static void usage() {
    System.out.println(String.format("Usage: %s command [options]", VCS_NAME));
  }
}
