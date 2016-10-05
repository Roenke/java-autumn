package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;

import java.io.File;
import java.nio.file.Path;
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
 * status
 * checkout branch_name|revision_hash
 * revert TODO: replace for reset
 * TODO: reset [file] {revert file or revert all files to current revision status}
 * TODO: merge branch {merge branch into current branch}
 *
 * TODO: add exceptions
 *
 * Solutions:
 * Store only diff for each commit.
 */
public class EntryPoint {
  public static String VCS_NAME = "vcs";
  public static void main(String[] args) {
    if (args.length == 0) {
      usage();
      return;
    }

    Path currentDirectory = new File(System.getProperty("user.dir")).toPath();
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
