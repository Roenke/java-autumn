package com.spbau.bibaev.benchmark.server.sorting;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Vitaliy.Bibaev
 */
public class InsertionSorterTest {
  @Test
  public void empty() {
    test(new int[]{});
  }

  @Test
  public void single() {
    test(new int[]{1});
  }

  @Test
  public void twoSorted() {
    test(new int[]{1, 2});
  }

  @Test
  public void twoReversed() {
    test(new int[]{2, 1});
  }

  @Test
  public void threeShuffled() {
    test(new int[]{3, 1, 2});
  }

  @Test
  public void sameElements() {
    test(new int[]{1, 1, 1, 1, 1, 1, 1});
  }

  @Test
  public void twoSame() {
    test(new int[]{1, 2, 1, 2});
  }

  @Test
  public void random() {
    test(new Random().ints().limit(1000).toArray());
  }

  private void test(int[] array) {
    int[] cp = array.clone();
    Arrays.sort(cp);
    InsertionSorter.sort(array);
    assertArrayEquals(array, cp);
  }
}
