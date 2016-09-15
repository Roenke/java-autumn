package com.spbau.bibaev.homework.vcs.command;

import com.spbau.bibaev.homework.vcs.command.impl.UserCommand;
import com.spbau.bibaev.homework.vcs.repository.Repository;
import com.spbau.bibaev.homework.vcs.command.impl.InitCommand;
import com.spbau.bibaev.homework.vcs.command.impl.LogCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  }

  @Nullable
  public static Command createCommand(@NotNull String name, @NotNull Repository rep) {
    if (!NAME_2_COMMAND_CLASS_MAP.containsKey(name)) {
      return null;
    }

    Class<?> clazz = NAME_2_COMMAND_CLASS_MAP.get(name);
    CommandBase instance = null;
    try {
      Constructor<?> constructor = clazz.getConstructor(Repository.class);
      instance = (CommandBase) constructor.newInstance(rep);
    } catch (NoSuchMethodException | IllegalAccessException |
        InstantiationException | InvocationTargetException ignored) {
    }

    return instance;
  }
}
