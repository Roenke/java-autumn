package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class LazyFactoryMultiThreadTest extends LazyFactoryTestBase {
  @Override
  Lazy getLazy(@NotNull Supplier supplier) {
    return LazyFactory.createMultithreadedLazy(supplier);
  }

  @Test
  public void oneTimeSupplierCallTest() throws BrokenBarrierException, InterruptedException {
    AtomicInteger callCounter = new AtomicInteger(0);
    CyclicBarrier barrier = new CyclicBarrier(50);
    CyclicBarrier endBarrier = new CyclicBarrier(51);
    Lazy lazy = LazyFactory.createMultithreadedLazy(() -> {
      callCounter.incrementAndGet();
      return new Object();
    });
    long count = Stream.generate(() -> new Thread(() -> {
      try {
        barrier.await();
        lazy.get();
        endBarrier.await();
      } catch (InterruptedException | BrokenBarrierException ignored) {
      }

    })).peek(Thread::start).limit(50).count();

    endBarrier.await();
    assertEquals(50, count);
    assertEquals(1, callCounter.get());
  }
}
