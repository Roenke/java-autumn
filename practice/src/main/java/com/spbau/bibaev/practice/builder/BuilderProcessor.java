package com.spbau.bibaev.practice.builder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vitaliy.Bibaev
 */
public class BuilderProcessor extends AbstractProcessor {
  private Messager myMessenger;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    myMessenger = processingEnv.getMessager();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return new HashSet<>(Collections.singletonList(Builder.class.getCanonicalName()));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_8;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    myMessenger.printMessage(Diagnostic.Kind.NOTE, "Hello");
    myMessenger.printMessage(Diagnostic.Kind.NOTE, String.format("count = %d", annotations.size()));
    return false;
  }
}
