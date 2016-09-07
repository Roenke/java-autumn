package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public class LazyFactory {

  @NotNull
  public static <T> Lazy<T> createSingleThreadLazy(@NotNull Supplier<T> supplier) {
    return new Lazy<T>() {
      private ResultWrapper<T> myValue = null;

      @Override
      public T get() {
        if (myValue == null) {
          T value = supplier.get();
          myValue = new ResultWrapper<>(value);
        }

        return myValue.get();
      }
    };
  }

  @NotNull
  public static <T> Lazy<T> createMultithreadedLazy(@NotNull Supplier<T> supplier) {
    return new Lazy<T>() {
      private volatile ResultWrapper<T> myValue = null;

      @Override
      public T get() {
        if (myValue == null) {
          synchronized (this) {
            if (myValue == null) {
              myValue = new ResultWrapper<>(supplier.get());
            }
          }
        }

        return myValue.get();
      }
    };
  }

  @NotNull
  public static <T> Lazy<T> createLockFreeLazy(@NotNull Supplier<T> supplier) {
    return new Lazy<T>() {
      private final AtomicReference<ResultWrapper<T>> myValue = new AtomicReference<>();

      @Override
      public T get() {
        if (myValue.get() == null) {
          T value = supplier.get();
          myValue.compareAndSet(null, new ResultWrapper<>(value));
        }

        return myValue.get().get();
      }
    };
  }

  private static class ResultWrapper<T> {
    private final T myResult;

    ResultWrapper(@Nullable T result) {
      myResult = result;
    }

    @Nullable
    T get() {
      return myResult;
    }
  }
}
