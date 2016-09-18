package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement
public class Branch {
  @XmlElement(name = "name")
  private final String myName;
  @XmlElement(name = "revisions")
  private final List<Revision> myRevisions;

  public Branch(@NotNull String name, @NotNull List<Revision> revisions) {
    myName = name;
    myRevisions = revisions;
  }

  public static Branch read(@NotNull File dir) throws RepositoryOpeningException {
    String name = dir.getName();
    List<Revision> revisions = new ArrayList<>();

    File[] files = dir.listFiles(File::isDirectory);
    if (files != null) {
      for (File file : files) {
        revisions.add(Revision.read(file));
      }
    }

    return new Branch(name, revisions);
  }

  public static Branch createNewBranch(@NotNull File metadataDirectory, @NotNull String name) throws RepositoryIOException {
    File branchDirectory = new File(metadataDirectory.getAbsoluteFile() + File.separator + name);
    if (!branchDirectory.mkdir()) {
      throw new RepositoryIOException(String.format("Branch \"%s\" already exists", name));
    }

    return new Branch(name, new ArrayList<>());
  }

  public static Branch createNewBranch(@NotNull File metadataDirectory, @NotNull String name, @NotNull Branch base) {
    // TODO: copy revision from base to new branch
    return null;
  }

  @NotNull
  public List<Revision> getRevisions() {
    return Collections.unmodifiableList(myRevisions);
  }

  @NotNull
  public String getName() {
    return myName;
  }
}
