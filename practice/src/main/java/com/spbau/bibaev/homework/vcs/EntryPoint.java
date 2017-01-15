package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.command.Command;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * reset [file] {revert file or revert all files to current revision status}
 * <p>
 * Solutions:
 * Store only diff for each commit.
 */
public class EntryPoint {
  public static final String VCS_NAME = "vcs";

  public static void main(String[] args) {
    if (args.length == 0) {
      usage();
      return;
    }

    final Path currentDirectory = Paths.get("user.dir");
    final Command command = CommandFactory.createCommand(currentDirectory, args[0]);
    if (command == null) {
      usage();
      return;
    }

    final List<String> commandArgs = Arrays.asList(args).subList(1, args.length);
    command.perform(commandArgs);
  }

  private static void usage() {
    System.out.println(String.format("Usage: %s command [options]", VCS_NAME));
  }
}
