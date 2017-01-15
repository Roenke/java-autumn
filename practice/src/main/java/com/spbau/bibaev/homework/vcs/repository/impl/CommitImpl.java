package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

public class CommitImpl implements Commit {
  private final Repository myRepository;
  private final List<Commit> myParents;
  private final List<FilePersistentState> myAddedFiles;
  private final List<FilePersistentState> myModifiedFiles;
  private final List<String> myDeletedFiles;
  private final CommitMeta myMeta;

  CommitImpl(@NotNull List<Commit> parents, @NotNull List<FilePersistentState> added,
             @NotNull List<FilePersistentState> modified, @NotNull List<String> deleted,
             @NotNull CommitMeta meta, @NotNull Repository repository) {
    myParents = parents;
    myAddedFiles = added;
    myModifiedFiles = modified;
    myDeletedFiles = deleted;
    myMeta = meta;
    myRepository = repository;
  }

  @NotNull
  @Override
  public Path getSnapshotFile() {
    return myRepository.getMetaDirectory().resolve(myMeta.getId());
  }

  @NotNull
  @Override
  public List<Commit> getParents() {
    return Collections.unmodifiableList(myParents);
  }

  @Nullable
  @Override
  public Commit getMainParent() {
    if (myParents.isEmpty()) {
      return null;
    }

    return myParents.get(0);
  }

  @NotNull
  @Override
  public CommitMeta getMeta() {
    return myMeta;
  }

  @NotNull
  @Override
  public List<FilePersistentState> getAddedFiles() {
    return Collections.unmodifiableList(myAddedFiles);
  }

  @NotNull
  @Override
  public List<FilePersistentState> getModifiedFiles() {
    return Collections.unmodifiableList(myModifiedFiles);
  }

  @NotNull
  @Override
  public List<String> getDeletedFiles() {
    return Collections.unmodifiableList(myDeletedFiles);
  }

  @NotNull
  @Override
  public RepositoryState getRepositoryState() {
    final LinkedList<Commit> commits = new LinkedList<>();
    Commit current = this;
    while (current != null) {
      commits.addFirst(current);
      current = current.getMainParent();
    }

    final Map<String, FilePersistentState> files = new HashMap<>();
    for (Commit commit : commits) {
      commit.getAddedFiles().forEach(x -> files.put(x.getRelativePath(), x));
      commit.getModifiedFiles().forEach(x -> files.put(x.getRelativePath(), x));
      commit.getDeletedFiles().forEach(files::remove);
    }

    final List<FilePersistentState> states = new ArrayList<>(files.values());
    return () -> Collections.unmodifiableList(states);
  }
}
