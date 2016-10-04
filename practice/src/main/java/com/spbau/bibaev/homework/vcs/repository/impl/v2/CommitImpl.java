package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CommitImpl implements Commit {
  private final RepositoryImpl myRepository;
  private final List<Commit> myParents;
  private final List<FilePersistentState> myAddedFiles;
  private final List<FilePersistentState> myModifiedFiles;
  private final List<FilePersistentState> myDeletedFiles;
  private final CommitMeta myMeta;

  public CommitImpl(@NotNull List<Commit> parents, @NotNull List<FilePersistentState> added,
                    @NotNull List<FilePersistentState> modified, @NotNull List<FilePersistentState> deleted,
                    @NotNull CommitMeta meta, @NotNull RepositoryImpl repository) {
    myParents = parents;
    myAddedFiles = added;
    myModifiedFiles = modified;
    myDeletedFiles = deleted;
    myMeta = meta;
    myRepository = repository;
  }

  public Path getSnapshotFile() {
    return myRepository.getMetaDirectory().resolve(myMeta.getId());
  }

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

  @Override
  public CommitMeta getMeta() {
    return myMeta;
  }

  @Override
  public List<FilePersistentState> getAddedFiles() {
    return Collections.unmodifiableList(myAddedFiles);
  }

  @Override
  public List<FilePersistentState> getModifiedFiles() {
    return Collections.unmodifiableList(myModifiedFiles);
  }

  @Override
  public List<FilePersistentState> getDeletedFiles() {
    return Collections.unmodifiableList(myDeletedFiles);
  }

  @Override
  public RepositoryState getRepositoryState() {
    LinkedList<Commit> commits = new LinkedList<>();
    Commit current = this;
    while (current != null) {
      commits.addFirst(current);
      List<Commit> parents = current.getParents();
      current = parents.isEmpty() ? null : parents.get(0);
    }

    Map<String, FilePersistentState> files = new HashMap<>();
    for (Commit commit : commits) {
      commit.getAddedFiles().forEach(x -> files.put(x.getRelativePath(), x));
      commit.getModifiedFiles().forEach(x -> files.put(x.getRelativePath(), x));
      commit.getDeletedFiles().forEach(x -> files.remove(x.getRelativePath()));
    }

    List<FilePersistentState> states = files.values().stream().collect(Collectors.toList());
    return () -> Collections.unmodifiableList(states);
  }
}
