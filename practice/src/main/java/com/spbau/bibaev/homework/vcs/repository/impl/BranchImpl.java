package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIllegalStateException;
import com.spbau.bibaev.homework.vcs.repository.api.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class BranchImpl implements Branch {
  private final String myName;
  private final List<RevisionImpl> myRevisions;
  private final File myBranchDirectory;

  private BranchImpl(@NotNull String name, @NotNull List<RevisionImpl> revisions, @NotNull File directory) {
    myName = name;
    myRevisions = new ArrayList<>(revisions);
    myBranchDirectory = directory;
    myRevisions.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
  }

  @NotNull
  public List<Revision> getRevisions() {
    return Collections.unmodifiableList(myRevisions);
  }

  @NotNull
  public Revision getLastRevision() {
    return Collections.max(myRevisions, (r1, r2) -> r1.getDate().compareTo(r2.getDate()));
  }

  @NotNull
  public String getName() {
    return myName;
  }

  static BranchImpl read(@NotNull File dir) throws IOException {
    String name = dir.getName();
    List<RevisionImpl> revisions = new ArrayList<>();

    File[] files = dir.listFiles(File::isDirectory);
    if (files != null) {
      for (File file : files) {
        revisions.add(RevisionImpl.read(file));
      }
    }

    return new BranchImpl(name, revisions, dir);
  }

  static BranchImpl createNewBranch(@NotNull Path metadataDirectory, @NotNull String name, @NotNull BranchImpl base)
      throws IOException {
    return createNewBranch(metadataDirectory, name, base.getRevisionsImpl());
  }

  static BranchImpl createNewBranch(@NotNull Path metadataDirectory, @NotNull String name,
                                    @NotNull Collection<RevisionImpl> revisions) throws IOException {
    File branchDirectory = new File(metadataDirectory.toFile().getAbsolutePath() + File.separator + name);
    if (!branchDirectory.mkdir()) {
      throw new RepositoryIllegalStateException(String.format("BranchImpl \"%s\" already exists", name));
    }

      if (revisions.isEmpty()) {
        File revisionDirectory = Files.createDirectory(branchDirectory.toPath()
            .resolve(String.valueOf(System.currentTimeMillis()))).toFile();
        RevisionImpl.createEmptyRevision(revisionDirectory);
      }

      for (RevisionImpl revision : revisions) {
        Path revisionPath = branchDirectory.toPath().resolve(revision.getHash());
        Files.createDirectory(revisionPath);
        FilesUtil.recursiveCopyDirectory(revision.getDirectory(), revisionPath);
      }

    return BranchImpl.read(branchDirectory);
  }

  List<RevisionImpl> getRevisionsImpl() {
    return Collections.unmodifiableList(myRevisions);
  }

  Revision commitChanges(ProjectImpl project, @NotNull String message,
                            @NotNull Date date, @NotNull String userName) throws IOException {
    Path path = Files.createDirectory(myBranchDirectory.toPath().resolve(String.valueOf(System.currentTimeMillis())));
    RevisionImpl revision = RevisionImpl.addNewRevision(project, path, message, date, userName);
    myRevisions.add(revision);

    return revision;
  }
}