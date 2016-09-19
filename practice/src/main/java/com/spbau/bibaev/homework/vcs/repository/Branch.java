package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

  static Branch createNewBranch(@NotNull File metadataDirectory, @NotNull String branchName, @NotNull String userName)
      throws RepositoryIOException {
    File branchDirectory = new File(metadataDirectory.getAbsoluteFile() + File.separator + branchName);
    if (!branchDirectory.mkdir()) {
      throw new RepositoryIOException(String.format("Branch \"%s\" already exists", branchName));
    }

    File initRevisionDirectory = new File(branchDirectory.getAbsolutePath() + File.separator +
        INITIAL_REVISION_DIRECTORY_NAME);
    if (!initRevisionDirectory.mkdir()) {
      throw new RepositoryIOException("Cannot create directory for initial revision in " +
          initRevisionDirectory.toString());
    }

    Revision.createEmptyRevision(initRevisionDirectory, userName);
    Revision revision = Revision.read(initRevisionDirectory);

    return new Branch(branchName, Collections.singletonList(revision), branchDirectory);
  }

  static Branch createNewBranch(@NotNull File metadataDirectory, @NotNull String name, @NotNull Branch base)
      throws RepositoryIOException {
    File branchDirectory = new File(metadataDirectory.getAbsoluteFile() + File.separator + name);
    if (!branchDirectory.mkdir()) {
      throw new RepositoryIOException(String.format("Branch \"%s\" already exists", name));
    }

    try {
      FilesUtil.recursiveCopyDirectory(base.myBranchDirectory.toPath(), branchDirectory.toPath());
    } catch (IOException e) {
      try {
        Files.delete(branchDirectory.toPath());
      } catch (IOException e1) {
        throw new RepositoryIOException("Repository corrupted", e1);
      }
      throw new RepositoryIOException(String.format("Could not recursive copy from %s to %s",
          base.myBranchDirectory.getAbsolutePath(), branchDirectory.getAbsolutePath()));
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