package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public class LazyFactory {

  @NotNull
  public static <T> Lazy<T> createSingleThreadLazy(@NotNull Supplier<T> supplier) {
    return new MySingleThreadLazy<>(supplier);
  }

  @NotNull
  public static <T> Lazy<T> createMultithreadedLazy(@NotNull Supplier<T> supplier) {
    return new MyMultithreadedLazy<>(supplier);
  }

  @NotNull
  public static <T> Lazy<T> createLockFreeLazy(@NotNull Supplier<T> supplier) {
    return new MyLockFreeLazy<>(supplier);
  }

  private static class MySingleThreadLazy<T> implements Lazy<T> {
    private Supplier<T> mySupplier;
    private T myValue;
    MySingleThreadLazy(@NotNull Supplier<T> internalSupplier) {
      mySupplier = internalSupplier;
    }

    @Override
    public T get() {
      if (mySupplier != null ) {
        myValue = mySupplier.get();
        mySupplier = null;
      }

      return myValue;
    }
  }

  private static class MyMultithreadedLazy<T> implements Lazy<T> {
    private volatile Supplier<T> mySupplier;
    private volatile T myValue = null;

    MyMultithreadedLazy(@NotNull Supplier<T> internalSupplier) {
      mySupplier = internalSupplier;
    }

    @Override
    public T get() {
      if (mySupplier != null) {
        synchronized (this) {
          if (mySupplier != null) {
            myValue = mySupplier.get();
            mySupplier = null;
          }
        }
      }

      return myValue;
    }
  }

  private static class MyLockFreeLazy<T> implements Lazy<T> {
    private volatile Object mySupplier;

    private final static AtomicReferenceFieldUpdater<MyLockFreeLazy, Object> FIELD_UPDATER =
        AtomicReferenceFieldUpdater.newUpdater(MyLockFreeLazy.class, Object.class, "mySupplier");

    MyLockFreeLazy(@NotNull Supplier<T> supplier) {
      mySupplier = new MySupplier<>(supplier);
    }

    @Override
    public T get() {
      Object supplier = mySupplier;
      if(mySupplier != null && mySupplier instanceof MySupplier) {
        //noinspection unchecked
        Object t = ((Supplier<Supplier<?>>) mySupplier).get().get();
        FIELD_UPDATER.compareAndSet(this, supplier, t);
      }

      //noinspection unchecked
      return (T) mySupplier;
    }
  }

  private static class MySupplier<T> implements Supplier<Supplier<T>> {
    private final Supplier<T> mySupplier;
    MySupplier(@NotNull Supplier<T> supplier) {
      mySupplier = supplier;
    }

    @NotNull
    @Override
    public Supplier<T> get() {
      return mySupplier;
    }
  }
}
