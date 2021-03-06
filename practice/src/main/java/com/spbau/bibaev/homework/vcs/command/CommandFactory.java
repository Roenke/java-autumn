package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.command.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
  private static final Map<String, Class<?>> NAME_2_COMMAND_CLASS_MAP = new HashMap<>();

  static {
    NAME_2_COMMAND_CLASS_MAP.put("add", AddCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("log", LogCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("init", InitCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("user", UserCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("branch", BranchCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("commit", CommitCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("status", StatusCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("checkout", CheckoutCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("reset", ResetCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("rm", RmCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("merge", MergeCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("clean", CleanCommand.class);
  }

  @Nullable
  public static Command createCommand(@NotNull Path currentDirectory, @NotNull String name) {
    if (!NAME_2_COMMAND_CLASS_MAP.containsKey(name)) {
      return null;
    }

    Class<?> clazz = NAME_2_COMMAND_CLASS_MAP.get(name);
    CommandBase instance;
    try {
      Constructor<?> constructor = clazz.getConstructor(Path.class);
      instance = (CommandBase) constructor.newInstance(currentDirectory);
    } catch (NoSuchMethodException | IllegalAccessException |
        InstantiationException | InvocationTargetException ignored) {
      return null;
    }

    return instance;
  }
}
