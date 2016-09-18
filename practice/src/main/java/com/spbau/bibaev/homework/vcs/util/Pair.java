package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.Nullable;

public class Pair<A, B> {
  public final A first;
  public final B second;

  private Pair(@Nullable A fst, @Nullable B snd) {
    first = fst;
    second = snd;
  }

  public static <A, B> Pair<A, B> makePair(@Nullable A fst, @Nullable B snd) {
    return new Pair<>(fst, snd);
  }
}
