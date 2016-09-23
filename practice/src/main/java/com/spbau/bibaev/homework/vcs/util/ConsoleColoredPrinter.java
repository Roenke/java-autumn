package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConsoleColoredPrinter {
  public static final String GREEN = "\u001B[32m";
  public static final String WHITE = "\u001B[37m";
  public static final String YELLOW = "\u001B[33m";
  private static final String ANSI_RESET = "\u001B[0m";

  public enum Color {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    WHITE("\u001B[37m"),
    YELLOW("\u001B[33m");


    private final String myAnsiColorSequence;

    Color(@NotNull String color) {
      myAnsiColorSequence = color;
    }

    @NotNull
    private String color() {
      return myAnsiColorSequence;
    }
  }

  public static void println(@NotNull String text) {
    println(text, Color.WHITE);
  }

  public static void println(@NotNull String text, @NotNull Color color) {
    System.out.println(String.format("%s%s%s" , color.color(), text, ANSI_RESET));
  }

  public static void printListOfFiles(@NotNull String groupName, @NotNull Color color,
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
