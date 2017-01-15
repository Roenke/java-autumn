package com.spbau.bibaev.test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {
  private static final int LIST_SIZE = 10000000;

  private static class MySumTask extends RecursiveTask<BigInteger> {
    private static final int THRESHOLD = 10000;
    private final List<Integer> myElems;

    MySumTask(List<Integer> lst) {
      myElems = lst;
    }

    @Override
    protected BigInteger compute() {
      if(myElems.size() < THRESHOLD) {
        BigInteger res = new BigInteger("0");
        for(Integer num : myElems) {
          res = res.add(BigInteger.valueOf(num));
        }

        return res;
      }

      int mid = myElems.size() >>> 1;
      MySumTask left = new MySumTask(myElems.subList(0, mid));
      MySumTask right = new MySumTask(myElems.subList(mid, myElems.size()));

      ForkJoinTask.invokeAll(left, right);

      return left.join().add(right.join());
    }
  }

  public static void main(String[] args) throws IOException {
    ForkJoinPool pool = new ForkJoinPool(4);
    Random rand = new Random();
    List<Integer> lst = rand.ints().map(Math::abs).limit(LIST_SIZE).boxed().collect(Collectors.toList());

    for(int i = 0; i < 20; i++) {
      evalTime("fork-join pool evaluation", () -> {
        ForkJoinTask<BigInteger> submit = pool.submit(new MySumTask(lst));
        try {
          System.out.println(submit.get());
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      });

      evalTime("sequential evaluation", () -> {
        BigInteger res = new BigInteger("0");
        for(Integer val : lst) {
          res = res.add(BigInteger.valueOf(val));
        }

        System.out.println(res);
      });
    }
  }

  private static void evalTime(String description, Runnable task) {
    long begin = System.nanoTime();
    task.run();
    System.out.printf("Time of \"%s\" = %d ms%n", description ,TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
  }
}
