package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class LazyFactoryLockFreeTest extends LazyFactoryTestBase {

  @Test
  public void sameObjectSharedTest() throws BrokenBarrierException, InterruptedException {
    AtomicInteger callCounter = new AtomicInteger(0);
    CyclicBarrier supplierBarrier = new CyclicBarrier(50);
    Lazy lazy = LazyFactory.createLockFreeLazy (() -> {
      try {
        supplierBarrier.await();
      } catch (InterruptedException | BrokenBarrierException ignored) {
      }
      callCounter.incrementAndGet();
      return new Object();
    });

    CyclicBarrier barrier = new CyclicBarrier(50);
    CyclicBarrier completeBarrier = new CyclicBarrier(51);
    List<Object> lazyResults = new CopyOnWriteArrayList<>();
    final long completeCount = Stream.generate(() -> new Thread(() -> {
      try {
        barrier.await();
        lazyResults.add(lazy.get());
        completeBarrier.await();
      } catch (InterruptedException | BrokenBarrierException ignored) {
      }
    })).peek(Thread::start).limit(50).count();

    completeBarrier.await();

    assertEquals(50, completeCount);
    assertNotEquals(1, callCounter.get());
    assertEquals(50, lazyResults.size());
    Object first = lazyResults.get(0);
    lazyResults.forEach(o -> assertSame(first, o));
  }

  @Override
  Lazy getLazy(@NotNull Supplier supplier) {
    return LazyFactory.createLockFreeLazy(supplier);
  }
}
