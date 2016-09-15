package com.spbau.bibaev.practice.first;

import com.sun.istack.internal.NotNull;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
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
    Lazy lazy = LazyFactory.createLockFreeLazy(() -> {
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

  @Test
  public void everyThreadCallZeroOrOneOneTimeTest() throws BrokenBarrierException, InterruptedException {
    Map<Thread, Integer> callCountMap = new ConcurrentHashMap<>();

    Supplier supplier = () -> {
      Thread currentThread = Thread.currentThread();
      callCountMap.put(currentThread, callCountMap.get(currentThread) + 1);
      return new Object();
    };

    Lazy lazy = getLazy(supplier);
    CyclicBarrier barrier = new CyclicBarrier(50);
    CyclicBarrier endBarrier = new CyclicBarrier(51);
    List<Object> lazyResults = new CopyOnWriteArrayList<>();
    Stream.generate(() -> new Thread(() -> {
      try {
        callCountMap.put(Thread.currentThread(), 0);
        barrier.await();
        lazyResults.add(lazy.get());
        lazyResults.add(lazy.get());
        endBarrier.await();
      } catch (InterruptedException | BrokenBarrierException ignored) {
      }
    })).peek(Thread::start).limit(50).count();

    endBarrier.await();
    callCountMap.values().forEach(integer -> assertTrue(integer < 2));
    assertEquals(100, lazyResults.size());
    Object first = lazyResults.get(0);
    lazyResults.forEach(o -> assertSame(first, o));
  }

  @Test
  public void subsequentCallsFromFewThreadsTest() throws BrokenBarrierException, InterruptedException {
    AtomicInteger callCounter = new AtomicInteger(0);
    Lazy lazy = getLazy(() -> {
      callCounter.incrementAndGet();
      return new Object();
    });

    CyclicBarrier barrier = new CyclicBarrier(2);
    CyclicBarrier endBarrier = new CyclicBarrier(3);
    new Thread(() -> {
      lazy.get();
      try {
        barrier.await();
        endBarrier.await();
      } catch (InterruptedException | BrokenBarrierException ignored) {
      }
    }).start();
    new Thread(() -> {
      try {
        barrier.await();
        lazy.get();
        endBarrier.await();
      } catch (InterruptedException | BrokenBarrierException ignored) {
      }
    }).start();

    endBarrier.await();
    assertEquals(1, callCounter.get());
  }

  @Override
  Lazy getLazy(@NotNull Supplier supplier) {
    return LazyFactory.createLockFreeLazy(supplier);
  }
}
