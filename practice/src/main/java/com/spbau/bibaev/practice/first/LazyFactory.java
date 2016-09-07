package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyFactory {

  public static <T> Lazy<T> createSingleThreadLazy(@NotNull Supplier<T> supplier) {
    return new Lazy<T>() {
      private T myValue;

      @Override
      public T get() {
        if (myValue == null) {
          myValue = supplier.get();
        }

        return myValue;
      }
    };
  }

  public static <T> Lazy<T> createMultithreadedLazy(@NotNull Supplier<T> supplier) {
    return new Lazy<T>() {
      private volatile T myValue = null;

      @Override
      public T get() {
        if (myValue == null) {
          synchronized (this) {
            if (myValue == null) {
              myValue = supplier.get();
            }
          }
        }

        return myValue;
      }
    };
  }

  public static <T> Lazy<T> createLockFreeLazy(@NotNull Supplier<T> supplier) {
    return new Lazy<T>() {
      private final AtomicReference<Optional<T>> myValue = new AtomicReference<>(Optional.empty());

      @Override
      public T get() {
        if (!myValue.get().isPresent()) {
          T value = supplier.get();
          myValue.compareAndSet(Optional.empty(), Optional.of(value));
        }

        return myValue.get().orElse(null);
      }
    };
  }
}
