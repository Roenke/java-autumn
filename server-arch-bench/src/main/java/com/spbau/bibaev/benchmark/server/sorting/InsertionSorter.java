package com.spbau.bibaev.benchmark.server.sorting;

/**
 * @author Vitaliy.Bibaev
 */
public class InsertionSorter {
  public static void sort(int[] array) {
    for (int i = 0; i < array.length; i++) {
      int minIndex = i;
      for (int j = i + 1; j < array.length; j++) {
        if (array[j] < array[minIndex]) {
          minIndex = j;
        }
      }

      int tmp = array[i];
      array[i] = array[minIndex];
      array[minIndex] = tmp;
    }
  }
}
