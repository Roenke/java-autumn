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
  private final String myName;
  private final List<Revision> myRevisions;
  private final File myBranchDirectory;

  private Branch(@NotNull String name, @NotNull List<Revision> revisions, @NotNull File directory) {
    myName = name;
    myRevisions = revisions;
    myBranchDirectory = directory;
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

  static Branch createNewBranch(@NotNull File metadataDirectory, @NotNull String name) throws RepositoryIOException {
    File branchDirectory = new File(metadataDirectory.getAbsoluteFile() + File.separator + name);
    if (!branchDirectory.mkdir()) {
      throw new RepositoryIOException(String.format("Branch \"%s\" already exists", name));
    }

    return new Branch(name, new ArrayList<>(), branchDirectory);
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
  public String getName() {
    return myName;
  }

  public void commit(@NotNull String message, @NotNull Date date, String myCurrentUserName) throws RepositoryIOException {
    File revisionDirectory = new File(myBranchDirectory.getAbsolutePath() + File.separator + date.getTime());
    if(!revisionDirectory.mkdir()) {
      throw new RepositoryIOException("Cannot create directory for new revision");
    }

    try{
      String hashCode = Revision.addNewRevision(revisionDirectory, message, date, myCurrentUserName);
      if(revisionDirectory.renameTo(new File(hashCode))) {
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