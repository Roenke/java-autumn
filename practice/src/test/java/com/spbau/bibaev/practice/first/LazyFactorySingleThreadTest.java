package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;

import java.util.function.Supplier;

public class LazyFactorySingleThreadTest extends LazyFactoryTestBase {
  @Override
  Lazy getLazy(@NotNull Supplier supplier) {
    return LazyFactory.createSingleThreadLazy(supplier);
  }
}
