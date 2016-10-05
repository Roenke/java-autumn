package com.spbau.bibaev.homework.vcs.repository.api;

import java.util.Collection;

public interface RepositoryIndex {
  Collection<String> added();
  Collection<String> removed();
}
