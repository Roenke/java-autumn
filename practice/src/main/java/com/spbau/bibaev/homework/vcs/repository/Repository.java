package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import com.spbau.bibaev.homework.vcs.util.XmlSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Repository {
  private static final String VCS_DIRECTORY_NAME = ".my_vcs";
  private static final String DEFAULT_BRANCH_NAME = "master";
  private static final String METADATA_FILENAME = "metadata.xml";
  private static final String DEFAULT_USER_NAME = System.getProperty("user.name");
  private final Map<String, Branch> myName2Branch;
  private final Project myProject;
  private String myCurrentBranchName;
  private String myCurrentUserName;
  private File myRepositoryMetadataDirectory;

  @NotNull
  public static Repository open(@NotNull File directory) throws RepositoryOpeningException {
    File currentDirectory = directory;
    while (currentDirectory != null && !FilesUtil.isContainsDirectory(currentDirectory, VCS_DIRECTORY_NAME)) {
      currentDirectory = currentDirectory.getParentFile();
    }

    if (currentDirectory == null) {
      throw new RepositoryOpeningException("Could not find repository root in " + directory.getAbsolutePath());
    }

    return openHere(currentDirectory);
  }

  private static Repository openHere(@NotNull File directory) throws RepositoryOpeningException {
    File metadataDirectory = FilesUtil.findDirectoryByName(directory, VCS_DIRECTORY_NAME);
    if (metadataDirectory == null) {
      throw new RepositoryOpeningException("Could not find repository root in " + directory.getAbsolutePath());
    }

    File[] branchDirectories = metadataDirectory.listFiles(File::isDirectory);
    if (branchDirectories == null) {
      throw new RepositoryOpeningException("Could not read any branch information");
    }

    Map<String, Branch> branches = new HashMap<>();
    for (File file : branchDirectories) {
      Branch branch = Branch.read(file);
      branches.put(branch.getName(), branch);
    }

    File metadataFile = FilesUtil.findFileByName(metadataDirectory, METADATA_FILENAME);
    if (metadataFile == null) {
      throw new RepositoryOpeningException("Repository metadata file not found");
    }
    RepositoryMetadata meta;
    try {
      meta = XmlSerializer.deserialize(metadataFile, RepositoryMetadata.class);
    } catch (JAXBException e) {
      throw new RepositoryOpeningException("Could not read from repository metadata file");
    }

    Project project = Project.open(directory, metadataDirectory);
    return new Repository(metadataDirectory, meta, branches, project);
  }

  @Nullable
  public Branch getBranchByName(@NotNull String name) {
    return myName2Branch.getOrDefault(name, null);
  }

  @NotNull
  public Branch getCurrentBranch() {
    return myName2Branch.get(myCurrentBranchName);
  }

  @NotNull
  public Project getProject() {
    return myProject;
  }

  @NotNull
  public List<Branch> getAllBranches() {
    return new ArrayList<>(myName2Branch.values());
  }

  @NotNull
  public String getCurrentBranchName() {
    return myCurrentBranchName;
  }

  public static void createNewRepository(@NotNull File directory) throws RepositoryIOException {
    File metadataDirectory = new File(directory.getAbsolutePath() + File.separator + VCS_DIRECTORY_NAME);
    if (metadataDirectory.exists() || !metadataDirectory.mkdir()) {
      throw new RepositoryIOException("Repository in \"" + directory.getAbsolutePath() + "\" already exists");
    }

    Branch.createNewBranch(metadataDirectory, DEFAULT_BRANCH_NAME, DEFAULT_USER_NAME);

    File metadataFile = new File(metadataDirectory.getAbsolutePath() + File.separator + METADATA_FILENAME);
    try {
      if (!metadataFile.createNewFile()) {
        throw new RepositoryIOException("Repository metadata file already exists");
      }

      XmlSerializer.serialize(metadataFile, RepositoryMetadata.class, RepositoryMetadata.defaultMeta());
    } catch (IOException e) {
      throw new RepositoryIOException("Could not create repository metadata file", e);
    } catch (JAXBException e) {
      throw new RepositoryIOException("Could not serialize repository metadata", e);
    }
  }

  private Repository(@NotNull File metaDirectory, @NotNull RepositoryMetadata meta,
                     @NotNull Map<String, Branch> branches, @NotNull Project project) {
    myRepositoryMetadataDirectory = metaDirectory;
    myName2Branch = branches;
    myProject = project;
    myCurrentBranchName = meta.currentBranch;
    myCurrentUserName = meta.userName;
  }

  public void save() throws RepositoryIOException {
    try {
      File metadataFile = new File(myRepositoryMetadataDirectory.getAbsolutePath() + File.separator +
          METADATA_FILENAME);
      XmlSerializer.serialize(metadataFile, RepositoryMetadata.class,
          new RepositoryMetadata(myCurrentBranchName, myCurrentUserName));
    } catch (JAXBException e) {
      throw new RepositoryIOException("Could not save repository meta", e);
    }
  }

  public String getUserName() {
    return myCurrentUserName;
  }

  public void setUserName(@NotNull String newName) {
    myCurrentUserName = newName;
  }

  public void createNewBranch(@NotNull String name) throws RepositoryIOException {
    Branch.createNewBranch(myRepositoryMetadataDirectory, name, myName2Branch.get(myCurrentBranchName));
  }

  public void commitChanges(@NotNull String message) throws RepositoryIOException {
    Date date = new Date();
    Branch currentBranch = myName2Branch.get(myCurrentBranchName);
    currentBranch.commit(message, date, myCurrentUserName);
  }

  @XmlRootElement
  private static class RepositoryMetadata {
    @XmlElement(name = "branch")
    String currentBranch;
    @XmlElement(name = "user")
    String userName;

    @SuppressWarnings("unused")
    RepositoryMetadata() {
    }

    RepositoryMetadata(@NotNull String branch, @NotNull String user) {
      currentBranch = branch;
      userName = user;
    }

    static RepositoryMetadata defaultMeta() {
      return new RepositoryMetadata(DEFAULT_BRANCH_NAME, DEFAULT_USER_NAME);
    }
  }
}
