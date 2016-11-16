package com.spbau.bibaev.practice.builder;

class A {
  @Builder
  public static A generate() {
    return new A();
  }

  @Builder
  public A() {

  }
}

/**
 * @author Vitaliy.Bibaev
 */
public class EntryPoint {
  public static void main(String[] args) {

  }
}
