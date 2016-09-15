package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.command.CommandBase;
import com.spbau.bibaev.homework.vcs.command.CommandFactory;
import com.spbau.bibaev.homework.vcs.repository.Repository;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supporting commands:
 * init
 * user [name]
 * TODO: log
 * TODO: add file file ...
 * TODO: remove file file ...
 * TODO: status
 * TODO: branch [delete] name
 * TODO: checkout branch_name
 * TODO: commit [message]
 * TODO: merge [branch]
 */
public class EntryPoint {

  public static void main(String[] args) {
    if (args.length == 0) {
      usage();
      return;
    }

    File currentDirectory = new File(System.getProperty("user.dir"));
    Repository repository = Repository.open(currentDirectory);
    CommandBase command = CommandFactory.createCommand(args[0], repository);
    if (command == null) {
      usage();
      return;
    }

    List<String> commandArgs = Arrays.stream(args).skip(1).collect(Collectors.toList());
    command.perform(commandArgs);
  }

  private static void usage() {
    System.out.println("Usage: my_cvs command [options]");
  }
}
