package com.spbau.bibaev.practice.junit.test;

import com.spbau.bibaev.practice.junit.annotation.Test;

public class SimpleTest {
  @Test
  public void test1() {
    int a = 100;
    int b = 1000;
    int c = b - a;
    System.out.println(c);
  }

  @Test
  public void test2() {
    int a = 0;
    int b = 20 / a;
    System.out.println(b);
  }
}
