package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConsoleColoredPrinter {
  private static final String ANSI_RESET = "\u001B[0m";

  public enum Color {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    WHITE("\u001B[37m"),
    YELLOW("\u001B[33m");


    final String myAnsiColorSequence;

    Color(@NotNull String color) {
      myAnsiColorSequence = color;
    }

    @NotNull
    String color() {
      return myAnsiColorSequence;
    }
  }

  public static void println(@NotNull String text) {
    println(text, Color.WHITE);
  }

  public static void println(@NotNull String text, @NotNull Color color) {
    System.out.println(String.format("%s%s%s", color.color(), text, ANSI_RESET));
  }

  public static void print(@NotNull String groupName, @NotNull Color color, @NotNull Collection<String> strings) {
    if (strings.isEmpty()) {
      return;
    }

    ConsoleColoredPrinter.println(String.format("%s:", groupName));
    for (String path : strings) {
      ConsoleColoredPrinter.println(String.format("\t\t%s", path), color);
    }
  }
}
