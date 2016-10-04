package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.repository.api.v2.Branch;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.v2.WorkingDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositoryImpl implements Repository, Serializable {
  private static final String DEFAULT_USERNAME = System.getProperty("user.name");
  private static final String DEFAULT_BRANCH_NAME = "master";

  private transient WorkingDirectory myWorkingDirectory;
  private final Map<String, String> myBranches = new HashMap<>();
  private final List<String> myAddedFiles = new ArrayList<>();
  private final List<String> myDeletedFiles = new ArrayList<>();
  private final Map<String, Commit> myCommitsIndex = new HashMap<>();
  private String myUserName = DEFAULT_USERNAME;
  private String myCurrentBranchName = DEFAULT_BRANCH_NAME;

  public static RepositoryImpl readRepository(@NotNull Path repositoryFile, @NotNull Path repositoryRoot)
      throws IOException, ClassNotFoundException {
    ObjectInputStream in = new ObjectInputStream(Files.newInputStream(repositoryFile));

    final RepositoryImpl repository = (RepositoryImpl) in.readObject();
    repository.myWorkingDirectory = new WorkingDirectoryImpl(repositoryRoot);

    return repository;
  }

  public static RepositoryImpl openRepository(@NotNull Path directory) {
    RepositoryImpl repository = new RepositoryImpl();
    repository.myWorkingDirectory = new WorkingDirectoryImpl(directory);
    return repository;
  }

  public void writeObject(@NotNull Path repositoryFile) throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(repositoryFile));
    out.writeObject(this);
  }

  @Override
  public WorkingDirectory getProject() {
    return myWorkingDirectory;
  }

  @Override
  public List<Branch> getBranches() {
    List<Branch> result = new ArrayList<>();
    for (String branchName : myBranches.keySet()) {
      String commitId = myBranches.get(branchName);
      Commit commit = myCommitsIndex.get(commitId);

      BranchImpl branch = new BranchImpl(branchName, commit);
      result.add(branch);
    }

    return result;
  }

  @Override
  public String getUserName() {
    return myUserName;
  }

  @Override
  public void setUserName(@NotNull String userName) throws IOException {
    myUserName = userName;
  }

  @Override
  public Branch getCurrentBranch() {
    return getBranchByName(myCurrentBranchName);
  }

  @Override
  public Branch createNewBranch(@NotNull String name, @NotNull Commit commit) throws IOException {
    assert !myBranches.containsKey(name);
    String commitId = commit.getMeta().getId();
    myBranches.put(name, commitId);
    return getBranchByName(name);
  }

  @Override
  public @Nullable Branch getBranchByName(@NotNull String branchName) {
    Commit commit = myCommitsIndex.get(myBranches.get(branchName));
    return new BranchImpl(branchName, commit);
  }

  @Override
  public boolean addFileToIndex(@NotNull Path pathToFile) {
    String relativePath = myWorkingDirectory.getRootDirectory().relativize(pathToFile).toString();
    return !myAddedFiles.contains(relativePath) && myAddedFiles.add(relativePath);
  }

  @Override
  public Commit commitChanges(@NotNull String message) throws IOException {
    // TODO
    return null;
  }

  @Override
  public Commit merge(@NotNull Commit commit, @Nullable String message) {
    // TODO
    return null;
  }

  @Override
  public Commit checkout(@NotNull Branch branch) throws IOException {
    myCurrentBranchName = branch.getName();
    return branch.getCommit();
  }

  @Override
  public Commit checkout(@NotNull Commit commit) throws IOException {
    String branchName = getCommitBranchName(commit.getMeta().getId());
    Branch branch = getBranchByName(branchName);
    myCurrentBranchName = branchName;
    if (branch != null) {
      return branch.getCommit();
    }

    return createNewBranch(branchName, commit).getCommit();
  }

  private String getCommitBranchName(@NotNull String commitId) {
    return String.format("commit_%s", commitId);
  }
}
