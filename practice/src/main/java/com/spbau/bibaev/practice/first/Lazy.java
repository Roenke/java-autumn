package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.Nullable;

@SuppressWarnings("WeakerAccess")
public interface Lazy<T> {
  @Nullable
  T get();
}
