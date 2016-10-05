package com.spbau.bibaev.homework.vcs.repository.impl.v2;

import com.spbau.bibaev.homework.vcs.EntryPoint;
import com.spbau.bibaev.homework.vcs.repository.api.Diff;
import com.spbau.bibaev.homework.vcs.repository.api.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.v2.*;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class RepositoryImpl implements Repository, Serializable {
  public static final String REPOSITORY_DIRECTORY_NAME = '.' + EntryPoint.VCS_NAME;
  public static final String DEFAULT_BRANCH_NAME = "master";

  private static final String REPOSITORY_METADATA = "repo.meta";
  private static final String DEFAULT_USERNAME = System.getProperty("user.name");

  private transient WorkingDirectory myWorkingDirectory;
  private final Map<String, String> myBranches = new HashMap<>();
  private final List<String> myAddedFiles = new ArrayList<>();
  private final List<String> myDeletedFiles = new ArrayList<>();
  private final Map<String, Commit> myCommitsIndex = new HashMap<>();
  private String myUserName = DEFAULT_USERNAME;
  private String myCurrentBranchName = DEFAULT_BRANCH_NAME;

  private RepositoryImpl(@NotNull String branch, @NotNull WorkingDirectory workingDirectory) {
    myCurrentBranchName = branch;
    myWorkingDirectory = workingDirectory;
  }

  private static RepositoryImpl readRepository(@NotNull Path repositoryFile, @NotNull Path repositoryRoot)
      throws IOException {
    ObjectInputStream in = new ObjectInputStream(Files.newInputStream(repositoryFile));

    final RepositoryImpl repository;
    try {
      repository = (RepositoryImpl) in.readObject();
    } catch (ClassNotFoundException e) {
      throw new IOException("Cannot restore the repository state");
    }
    repository.myWorkingDirectory = new WorkingDirectoryImpl(repositoryRoot);

    return repository;
  }

  @Nullable
  public static RepositoryImpl openRepository(@NotNull Path directory) throws IOException {
    File currentDirectory = directory.toFile();
    while (currentDirectory != null && !FilesUtil.isContainsDirectory(currentDirectory, REPOSITORY_DIRECTORY_NAME)) {
      currentDirectory = currentDirectory.getParentFile();
    }

    if (currentDirectory == null) {
      return null;
    }

    return openHere(currentDirectory.toPath());
  }

  public static RepositoryImpl createRepository(@NotNull Path directory) throws IOException {
    final RepositoryImpl repository = new RepositoryImpl(DEFAULT_BRANCH_NAME, new WorkingDirectoryImpl(directory));
    CommitImpl commit = new CommitImpl(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList(), new CommitMetaImpl(String.valueOf(System.currentTimeMillis()),
        DEFAULT_USERNAME, new Date(), DigestUtils.sha1Hex(new byte[0]), "The initial commit"), repository);
    final Path commitFile = repository.getProject().getRootDirectory()
        .resolve(REPOSITORY_DIRECTORY_NAME).resolve(commit.getMeta().getId());
    Files.createDirectories(commitFile.getParent());
    Files.createFile(commitFile);
    repository.myCommitsIndex.put(commit.getMeta().getId(), commit);
    repository.myBranches.put(repository.myCurrentBranchName, commit.getMeta().getId());
    repository.writeObject();
    return repository;
  }

  private static RepositoryImpl openHere(@NotNull Path directory) throws IOException {
    final Path metadataDirectory = directory.resolve(REPOSITORY_DIRECTORY_NAME);
    final Path meta = metadataDirectory.resolve(REPOSITORY_METADATA);
    if (!meta.toFile().exists()) {
      throw new IOException("Repository meta file not found");
    }

    final RepositoryImpl repository = readRepository(meta, directory);
    repository.myWorkingDirectory = new WorkingDirectoryImpl(directory);
    return repository;
  }

  private void writeObject() throws IOException {
    Path repositoryFile = myWorkingDirectory.getRootDirectory()
        .resolve(REPOSITORY_DIRECTORY_NAME).resolve(REPOSITORY_METADATA);
    if (!repositoryFile.toFile().exists()) {
      Files.createDirectories(repositoryFile.getParent());
      Files.createFile(repositoryFile);
    }

    ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(repositoryFile));
    out.writeObject(this);
  }

  public Path getMetaDirectory() {
    return myWorkingDirectory.getRootDirectory().resolve(REPOSITORY_DIRECTORY_NAME);
  }

  @Override
  public void save() throws IOException {
    writeObject();
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
  public Commit getCommit(@NotNull String commitId) {
    return myCommitsIndex.getOrDefault(commitId, null);
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

  @Nullable
  @Override
  public Branch getBranchByName(@NotNull String branchName) {
    if (!myBranches.containsKey(branchName)) {
      return null;
    }

    Commit commit = myCommitsIndex.get(myBranches.get(branchName));
    return new BranchImpl(branchName, commit);
  }

  @Override
  public RepositoryIndex getIndex() {
    return new RepositoryIndex() {
      @Override
      public Collection<String> added() {
        return Collections.unmodifiableCollection(myAddedFiles);
      }

      @Override
      public Collection<String> removed() {
        return Collections.unmodifiableCollection(myDeletedFiles);
      }
    };
  }

  @Override
  public boolean addFileToIndex(@NotNull Path pathToFile) {
    String relativePath = myWorkingDirectory.getRootDirectory().relativize(pathToFile).toString();
    return !myAddedFiles.contains(relativePath) && myAddedFiles.add(relativePath);
  }

  @Override
  public Commit commitChanges(@NotNull String message) throws IOException {
    Diff diff = getProject().getDiff(getCurrentBranch().getCommit().getRepositoryState());
    Path root = myWorkingDirectory.getRootDirectory();
    List<Path> newFiles = new ArrayList<>();
    List<Path> modifiedFiles = new ArrayList<>();
    List<String> removedFiles = new ArrayList<>();

    for (String relativePath : myAddedFiles) {
      FileState fileState = diff.getFileState(relativePath);
      if (fileState == FileState.NEW) {
        newFiles.add(root.resolve(relativePath));
      }
      if (fileState == FileState.MODIFIED) {
        modifiedFiles.add(root.resolve(relativePath));
      }
    }

    for (String relativePath : myDeletedFiles) {
      FileState fileState = diff.getFileState(relativePath);
      if (fileState == FileState.DELETED) {
        removedFiles.add(root.resolve(relativePath).toString());
      }
    }

    Commit commit = commitFiles(newFiles, modifiedFiles, removedFiles, message);
    myAddedFiles.clear();
    myDeletedFiles.clear();
    myCommitsIndex.put(commit.getMeta().getId(), commit);
    myBranches.put(myCurrentBranchName, commit.getMeta().getId());
    return commit;
  }

  private Commit commitFiles(@NotNull List<Path> newFiles, @NotNull List<Path> modifiedFiles,
                             @NotNull List<String> removedFiles, @NotNull String message) throws IOException {
    Commit current = getCurrentBranch().getCommit();

    Path snapshot = Files.createFile(myWorkingDirectory.getRootDirectory().resolve(REPOSITORY_DIRECTORY_NAME)
        .resolve(String.valueOf(System.currentTimeMillis())));
    MessageDigest globalDigest = DigestUtils.getSha1Digest();

    List<FileStateImpl> newStatesImpl = new ArrayList<>();
    List<FileStateImpl> modifiedStatesImpl = new ArrayList<>();

    try (OutputStream os = new DigestOutputStream(Files.newOutputStream(snapshot), globalDigest)) {
      addFilesToSnapshot(modifiedFiles, os, addFilesToSnapshot(newFiles, os, 0, newStatesImpl), modifiedStatesImpl);
    }

    String commitHash = DigestUtils.sha1Hex(globalDigest.digest());
    snapshot.toFile().renameTo(snapshot.getParent().resolve(commitHash).toFile());

    List<FilePersistentState> newStates = newStatesImpl.stream().collect(Collectors.toList());
    List<FilePersistentState> modifiedStates = modifiedStatesImpl.stream().collect(Collectors.toList());

    CommitImpl commit = new CommitImpl(Collections.singletonList(current), newStates, modifiedStates, removedFiles,
        new CommitMetaImpl(commitHash, myUserName, new Date(), commitHash, message), this);

    newStatesImpl.forEach(x -> x.setCommit(commit));
    modifiedStatesImpl.forEach(x -> x.setCommit(commit));

    return commit;
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

  private int addFilesToSnapshot(@NotNull List<Path> files, @NotNull OutputStream snapshotStream,
                                                 int offset, @NotNull List<FileStateImpl> result) throws IOException {
    Path root = myWorkingDirectory.getRootDirectory();
    for (Path file : files) {
      MessageDigest fileDigest = DigestUtils.getSha1Digest();
      try (InputStream is = new DigestInputStream(Files.newInputStream(file), fileDigest)) {
        int len = IOUtils.copy(is, snapshotStream);
        String fileHash = DigestUtils.sha1Hex(fileDigest.digest());
        FileStateImpl state = new FileStateImpl(root.relativize(file).toString(), null, fileHash, offset, len);
        result.add(state);
        offset += len;
      }
    }

    return offset;
  }
}
