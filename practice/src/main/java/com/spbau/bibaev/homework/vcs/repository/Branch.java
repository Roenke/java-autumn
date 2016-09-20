package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Branch {
  private static final String INITIAL_REVISION_DIRECTORY_NAME = "initial";
  private final String myName;
  private final List<Revision> myRevisions;
  private final File myBranchDirectory;

  private Branch(@NotNull String name, @NotNull List<Revision> revisions, @NotNull File directory) {
    myName = name;
    myRevisions = new ArrayList<>(revisions);
    myBranchDirectory = directory;
    myRevisions.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
  }

  static Branch read(@NotNull File dir) throws RepositoryOpeningException {
    String name = dir.getName();
    List<Revision> revisions = new ArrayList<>();

    File[] files = dir.listFiles(File::isDirectory);
    if (files != null) {
      for (File file : files) {
        revisions.add(Revision.read(file));
      }
    }

    return new Branch(name, revisions, dir);
  }

  static Branch createNewBranch(@NotNull File metadataDirectory, @NotNull String name, @NotNull Branch base)
      throws RepositoryIOException {
    return createNewBranch(metadataDirectory.toPath(), name, base.getRevisions());
  }

  static Branch createNewBranch(@NotNull Path metadataDirectory, @NotNull String name,
                                @NotNull Collection<Revision> revisions) throws RepositoryIOException {
    File branchDirectory = new File(metadataDirectory.toFile().getAbsolutePath() + File.separator + name);
    if (!branchDirectory.mkdir()) {
      throw new RepositoryIOException(String.format("Branch \"%s\" already exists", name));
    }

    try {
      if (revisions.isEmpty()) {
        File revisionDirectory = Files.createDirectory(branchDirectory.toPath()
            .resolve(INITIAL_REVISION_DIRECTORY_NAME)).toFile();
        Files.createDirectory(revisionDirectory.toPath());
        Revision.createEmptyRevision(revisionDirectory);
      }

      for (Revision revision : revisions) {
        Path revisionPath = branchDirectory.toPath().resolve(revision.getHash());
        Files.createDirectory(revisionPath);
        FilesUtil.recursiveCopyDirectory(revision.getDirectory(), revisionPath);
      }
    } catch (IOException e) {
      try {
        if (branchDirectory.exists()) {
          Files.delete(branchDirectory.toPath());
        }
      } catch (IOException e1) {
        throw new RepositoryIOException("Repository was corrupted");
      }
      throw new RepositoryIOException("Cannot copy revisions into" + branchDirectory.getAbsolutePath());
    }

    return Branch.read(branchDirectory);
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

  void commit(@NotNull String message, @NotNull Date date, String userName) throws RepositoryIOException {
    File revisionDirectory = new File(myBranchDirectory.getAbsolutePath() + File.separator + date.getTime());
    if (!revisionDirectory.mkdir()) {
      throw new RepositoryIOException("Cannot create directory for new revision");
    }

    try {
      String hashCode = Revision.addNewRevision(revisionDirectory, message, date, userName);
      if (revisionDirectory.renameTo(new File(hashCode))) {
        throw new RepositoryIOException("Cannot rename revision folder to hash code");
      }
    } catch (RepositoryIOException e) {
      try {
        Files.delete(revisionDirectory.toPath());
        throw e;
      } catch (IOException e1) {
        throw new RepositoryIOException("Repository was corrupted", e1);
      }
    }
  }
}