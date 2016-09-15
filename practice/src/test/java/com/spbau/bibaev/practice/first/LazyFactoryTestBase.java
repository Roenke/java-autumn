package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

abstract class LazyFactoryTestBase {

  @Test
  public void lazinessTest() {
    AtomicInteger callCounter = new AtomicInteger(0);
    Supplier supplier = () -> {
      callCounter.incrementAndGet();
      return new Object();
    };

    assertEquals(0, callCounter.get());
    Lazy lazy = getLazy(supplier);
    assertEquals(0, callCounter.get());

    //noinspection unused
    Object object = lazy.get();
    assertEquals(1, callCounter.get());
  }

  @Test
  public void nullHandlingTest() {
    AtomicInteger callCounter = new AtomicInteger(0);
    Lazy lazy = getLazy(() -> {
      callCounter.incrementAndGet();
      return null;
    });

    assertEquals(0, callCounter.get());
    lazy.get();
    assertEquals(1, callCounter.get());
    lazy.get();
    assertEquals(1, callCounter.get());
  }

  @Test
  public void sameObjectTest() {
    Lazy lazy = getLazy(Object::new);
    assertSame(lazy.get(), lazy.get());
  }

  @Test
  public void supplierReturnsItself() {
    AtomicInteger callCounter = new AtomicInteger(0);
    Lazy lazy = getLazy(() -> {
      callCounter.incrementAndGet();
      return this;
    });

    assertEquals(0, callCounter.get());
    assertEquals(lazy.get(), lazy.get());
    assertEquals(1, callCounter.get());
  }

  abstract Lazy getLazy(@NotNull Supplier supplier);
}
