package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;

public class ConsoleColoredPrinter {
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";
  private static final String ANSI_RESET = "\u001B[0m";

  public static void println(@NotNull String text, @NotNull String color) {
    System.out.println(String.format("%s%s%s" , color, text, ANSI_RESET));
  }
}
