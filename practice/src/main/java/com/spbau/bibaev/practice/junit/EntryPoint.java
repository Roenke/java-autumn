package com.spbau.bibaev.practice.junit;

import com.spbau.bibaev.practice.junit.annotation.Test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntryPoint {
  public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    String filepath = args[0];

    File path = Paths.get(filepath).toFile();
    if (!path.exists()) {
      System.err.println("Could not find a a directory or file: " + path.getAbsolutePath());
      return;
    }

    if (!path.isDirectory()) {
      System.err.println("Path should be specify a directory: " + path.getAbsolutePath());
    }

    Path directory = path.toPath();
    File[] files = directory.toFile().listFiles();
    if (files == null) {
      System.err.println("Something went wrong");
    }

    List<URL> urls = new ArrayList<>();
    List<String> names = new ArrayList<>();
    for (File f : files) {
      urls.add(f.toURI().toURL());
      String name = f.getName();
      names.add("com.spbau.bibaev.practice.junit.test." + name.substring(0, name.indexOf(".class")));
    }

    int globalTotal = 0;
    int globalPassed = 0;
    URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
    for (String name : names) {
      Class<?> testClass = urlClassLoader.loadClass(name);
      System.out.println("Test " + testClass.getName());
      Method[] methods = testClass.getDeclaredMethods();
      int total = 0;
      int passed = 0;
      for (Method method : methods) {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        if (Arrays.stream(declaredAnnotations).filter(x -> x.annotationType().equals(Test.class)).findFirst().isPresent()) {
          total++;
          Object instance = testClass.newInstance();
          try {
            method.invoke(instance);
          } catch (InvocationTargetException e) {
            System.out.println("Test not passed: " + method.getName() + "Reason: " + e.getCause());
            continue;
          }
          passed++;
          System.out.println("Test passed: " + method.getName());
        }
      }
      System.out.println("summary for " + testClass.getName() + ": " + passed + " / " + total);
      globalPassed += passed;
      globalTotal += total;
      System.out.println();
    }

    System.out.println("All summary: " + globalPassed + " / " + globalTotal);
  }
}
