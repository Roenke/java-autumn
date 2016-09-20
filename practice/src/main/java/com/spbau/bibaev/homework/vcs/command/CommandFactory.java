package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.command.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
  private static final Map<String, Class<?>> NAME_2_COMMAND_CLASS_MAP = new HashMap<>();

  static {
    NAME_2_COMMAND_CLASS_MAP.put("log", LogCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("init", InitCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("user", UserCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("branch", BranchCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("commit", CommitCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("status", StatusCommand.class);
    NAME_2_COMMAND_CLASS_MAP.put("checkout", CheckoutCommand.class);
  }

  @Nullable
  public static Command createCommand(@NotNull File currentDirectory, @NotNull String name) {
    if (!NAME_2_COMMAND_CLASS_MAP.containsKey(name)) {
      return null;
    }

    Class<?> clazz = NAME_2_COMMAND_CLASS_MAP.get(name);
    CommandBase instance;
    try {
      Constructor<?> constructor = clazz.getConstructor(File.class);
      instance = (CommandBase) constructor.newInstance(currentDirectory);
    } catch (NoSuchMethodException | IllegalAccessException |
        InstantiationException | InvocationTargetException ignored) {
      return null;
    }

    return instance;
  }
}
