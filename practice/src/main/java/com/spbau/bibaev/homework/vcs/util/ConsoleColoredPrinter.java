package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConsoleColoredPrinter {
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";
  public static final String WHITE = "\u001B[37m";
  public static final String YELLOW = "\u001B[33m";
  private static final String ANSI_RESET = "\u001B[0m";

  public static void println(@NotNull String text) {
    println(text, WHITE);
  }

  public static void println(@NotNull String text, @NotNull String color) {
    System.out.println(String.format("%s%s%s" , color, text, ANSI_RESET));
  }

  public static void printListOfFiles(@NotNull String groupName, @NotNull String color,
                                      @NotNull Collection<String> paths) {
    if (paths.isEmpty()) {
      return;
    }

    ConsoleColoredPrinter.println(String.format("%s:", groupName));
    for (String path : paths) {
      ConsoleColoredPrinter.println(String.format("\t\t%s", path), color);
    }
  }
}
