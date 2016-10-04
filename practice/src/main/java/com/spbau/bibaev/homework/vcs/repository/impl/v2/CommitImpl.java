package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.CommitMeta;
import com.spbau.bibaev.homework.vcs.repository.api.v2.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.RepositoryState;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommitImpl implements Commit {
  private final List<Commit> myParents;
  private final List<FileState> myAddedFiles;
  private final List<FileState> myModifiedFiles;
  private final List<FileState> myDeletedFiles;
  private final CommitMeta myMeta;

  public CommitImpl(@NotNull List<Commit> parents, @NotNull List<FileState> added, @NotNull List<FileState> modified,
                    @NotNull List<FileState> deleted, @NotNull CommitMeta meta) {
    myParents = parents;
    myAddedFiles = added;
    myModifiedFiles = modified;
    myDeletedFiles = deleted;
    myMeta = meta;
  }

  @Override
  public List<Commit> getParents() {
    return Collections.unmodifiableList(myParents);
  }

  @Override
  public CommitMeta getMeta() {
    return myMeta;
  }

  @Override
  public List<FileState> getAddedFiles() {
    return Collections.unmodifiableList(myAddedFiles);
  }

  @Override
  public List<FileState> getModifiedFiles() {
    return Collections.unmodifiableList(myModifiedFiles);
  }

  @Override
  public List<FileState> getDeletedFiles() {
    return Collections.unmodifiableList(myDeletedFiles);
  }

  @Override
  public RepositoryState getRepositoryState() {
    LinkedList<Commit> commits = new LinkedList<>();
    Commit current = this;
    while(current != null) {
      commits.addFirst(current);
      List<Commit> parents = current.getParents();
      current = parents.isEmpty() ? null : parents.get(0);
    }

    Map<String, FileState> files = new HashMap<>();
    for(Commit commit : commits) {
      commit.getAddedFiles().forEach(x -> files.put(x.getRelativePath(), x));
      commit.getModifiedFiles().forEach(x -> files.put(x.getRelativePath(), x));
      commit.getDeletedFiles().forEach(x -> files.remove(x.getRelativePath()));
    }

    List<FileState> states = files.values().stream().collect(Collectors.toList());
    return () -> Collections.unmodifiableList(states);
  }
}
