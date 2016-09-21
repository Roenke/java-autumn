package com.spbau.bibaev.practice.second;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExampleTest {

  @Rule
  public MyRule rule = new MyRule();

  @Test
  public void errorHappened() throws InterruptedException {
    AtomicInteger counter = new AtomicInteger(5);
    List<Thread> threads = Stream.generate(() -> new Thread(() -> {
      rule.registerThread(Thread.currentThread());
      Example.division(10, counter.getAndDecrement());
    })).limit(10).peek(Thread::start).collect(Collectors.toList());

    for (Thread thread : threads) {
      thread.join();
    }
  }

  private static class MyRule implements TestRule {
    private final Collection<Thread> myRegisteredThreads = new CopyOnWriteArrayList<>();
    private final Map<Thread, List<Throwable>> myErrors = new ConcurrentHashMap<>();

    @Override
    public Statement apply(Statement base, Description description) {
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          base.evaluate();
          for (Thread thread : myRegisteredThreads) {
            assertEquals(String.format("Thread %s complete with error", thread.getName()), 0, myErrors.get(thread).size());
            assertFalse("Thread should be completed", thread.isAlive());
          }
        }
      };
    }

    void registerThread(Thread thread) {
      myErrors.put(thread, new CopyOnWriteArrayList<>());
      thread.setUncaughtExceptionHandler((t, e) -> myErrors.get(thread).add(e));
      myRegisteredThreads.add(thread);
    }
  }
}
