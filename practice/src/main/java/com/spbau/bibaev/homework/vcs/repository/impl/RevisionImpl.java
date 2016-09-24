package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.repository.api.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.repository.api.Snapshot;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import com.spbau.bibaev.homework.vcs.util.Pair;
import com.spbau.bibaev.homework.vcs.util.XmlSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class RevisionImpl implements Revision {
  private static final String REVISION_METADATA_FILENAME = "revision_meta.xml";
  private static final String SNAPSHOT_FILENAME = "snapshot.data";
  private static final String INITIAL_COMMIT_MESSAGE = "Initial commit";
  private final String myHash;
  private final String myMessage;
  private final String myAuthor;
  private final Date myDate;
  private final Map<String, String> myFile2Hash;
  private final RevisionSnapshot mySnapshot;
  private final Path myRevisionDirectory;

  private RevisionImpl(@NotNull Path directory, @NotNull RevisionMetadata meta, @NotNull File snapshotFile) {
    myRevisionDirectory = directory;
    myHash = meta.hash;
    myAuthor = meta.author;
    myMessage = meta.message;
    myFile2Hash = meta.file2Descriptor.keySet().stream()
        .collect(Collectors.toMap(Function.identity(), x -> meta.file2Descriptor.get(x).fileHash));
    myDate = meta.date;
    Map<String, Pair<Long, Long>> file2Pos = meta.file2Descriptor.keySet().stream()
        .collect(Collectors.toMap(Function.identity(),
            s -> Pair.makePair(meta.file2Descriptor.get(s).offset, meta.file2Descriptor.get(s).length)));
    mySnapshot = new RevisionSnapshot(snapshotFile.toPath(), file2Pos);
  }

  @NotNull
  public String getHash() {
    return myHash;
  }

  @NotNull
  public Date getDate() {
    return myDate;
  }

  @NotNull
  public String getMessage() {
    return myMessage;
  }

  @NotNull
  @Override
  public String getAuthorName() {
    return myAuthor;
  }

  @NotNull
  @Override
  public FileState getFileState(@NotNull Path relativePath) throws IOException {
    boolean isFileInRevision = myFile2Hash.containsKey(relativePath.toString());
    boolean isFileOnDisk = relativePath.toFile().exists();
    if (!isFileInRevision && isFileOnDisk) {
      return FileState.NEW;
    }

    if (isFileInRevision && !isFileOnDisk) {
      return FileState.DELETED;
    }

    if (isFileInRevision) {
      String currentFileHash = FilesUtil.evalHashOfFile(relativePath.toFile());
      String revisionHash = myFile2Hash.get(relativePath.toString());
      return currentFileHash.equals(revisionHash) ? FileState.NOT_CHANGED : FileState.MODIFIED;
    }

    return FileState.UNKNOWN;
  }

  @Override
  public String getHashOfFile(@NotNull String relativePath) {
    return myFile2Hash.get(relativePath);
  }

  @NotNull
  @Override
  public List<Path> getFilePaths() {
    return myFile2Hash.keySet().stream().map(s -> Paths.get(s)).collect(Collectors.toList());
  }

  @NotNull
  @Override
  public Snapshot getSnapshot() {
    return mySnapshot;
  }

  static Revision createEmptyRevision(@NotNull File revisionDirectory) throws IOException {
    RevisionMetadata meta = new RevisionMetadata();
    meta.author = RepositoryImpl.DEFAULT_USER_NAME;
    meta.date = new Date();
    meta.message = INITIAL_COMMIT_MESSAGE;
    meta.hash = "00000000000000000000";
    meta.file2Descriptor = new HashMap<>();
    Pair<File, File> files = createMetaAndSnapshotFiles(revisionDirectory);
    File metaFile = files.first;
    XmlSerializer.serialize(metaFile, RevisionMetadata.class, meta);

    return read(revisionDirectory);
  }

  static RevisionImpl read(@NotNull File dir) throws IOException {
    String revisionName = dir.getName();
    File metadataFile = FilesUtil.findFileByName(dir, REVISION_METADATA_FILENAME);
    if (metadataFile == null) {
      throw new IOException("Metadata file not found for revision" + revisionName);
    }

    RevisionMetadata meta;
    meta = XmlSerializer.deserialize(metadataFile, RevisionMetadata.class);

    File snapshotFile = FilesUtil.findFileByName(dir, SNAPSHOT_FILENAME);
    if (snapshotFile == null) {
      throw new IOException("Snapshot file not found for revision " + revisionName);
    }

    return new RevisionImpl(dir.toPath(), meta, snapshotFile);
  }

  Path getDirectory() {
    return myRevisionDirectory;
  }

  @Nullable
  static RevisionImpl addNewRevision(@NotNull ProjectImpl project, @NotNull Path revisionDirectory,
                                     @NotNull String message, @NotNull Date date,
                                     @NotNull String user) throws IOException {
    RevisionMetadata meta = new RevisionMetadata();
    meta.author = user;
    meta.date = date;
    meta.message = message;

    Map<Path, String> path2Hash = new HashMap<>();
    Map<Path, Pair<Long, Long>> path2OffsetAndLength = new HashMap<>();
    Base64.Encoder encoder = Base64.getEncoder();

    Path projectRoot = project.getRootDirectory();
    MessageDigest globalDigest = getMD5Digest();
    MessageDigest fileDigest = getMD5Digest();
    if (globalDigest == null || fileDigest == null) {
      return null;
    }

    Pair<File, File> files = createMetaAndSnapshotFiles(revisionDirectory.toFile());
    File metaRevisionFile = files.first;
    File snapshot = files.second;

    OutputStream snapshotStream = new DigestOutputStream(Files.newOutputStream(snapshot.toPath()), globalDigest);

    long offset = 0;
    for (Path file : project.getAllFiles()) {
      Path fileRelativePath = projectRoot.relativize(file);
      fileDigest.reset();
      InputStream inputStream = Files.newInputStream(file);
      DigestInputStream fileStream = new DigestInputStream(inputStream, fileDigest);
      long length = file.toFile().length();
      FilesUtil.copy(fileStream, snapshotStream);
      String fileHash = encoder.encodeToString(fileDigest.digest());
      path2Hash.put(fileRelativePath, fileHash);
      path2OffsetAndLength.put(fileRelativePath, Pair.makePair(offset, length));
      offset += length;
    }

    snapshotStream.close();

    meta.hash = encoder.encodeToString(globalDigest.digest());
    meta.file2Descriptor = path2Hash.keySet().stream()
        .collect(Collectors.toMap(Path::toString,
            p -> new RevisionMetadata.FileDescriptor(path2Hash.get(p),
                path2OffsetAndLength.get(p).first,
                path2OffsetAndLength.get(p).second)));

    XmlSerializer.serialize(metaRevisionFile, RevisionMetadata.class, meta);
    return read(revisionDirectory.toFile());
  }

  private static Pair<File, File> createMetaAndSnapshotFiles(@NotNull File revisionDirectory) throws IOException {
    File metaRevisionFile = new File(revisionDirectory.getAbsolutePath() + File.separator + REVISION_METADATA_FILENAME);
    File snapshot = new File(revisionDirectory.getAbsolutePath() + File.separator + "snapshot.data");
    if (!snapshot.createNewFile()) {
      throw new IOException("Cannot create file for snapshot");
    }
    if (!metaRevisionFile.createNewFile()) {
      throw new IOException("Cannot create meta file for revision");
    }

    return Pair.makePair(metaRevisionFile, snapshot);
  }

  @XmlRootElement
  private static class RevisionMetadata {
    @XmlElement
    String message;
    @XmlElement
    String hash;
    @XmlElement
    String author;
    @XmlElement
    Date date;
    @XmlElement
    Map<String, FileDescriptor> file2Descriptor;

    @XmlRootElement
    static class FileDescriptor {
      @SuppressWarnings("unused")
      FileDescriptor() {
      }

      FileDescriptor(@NotNull String hash, long off, long len) {
        fileHash = hash;
        offset = off;
        length = len;
      }

      @XmlElement
      String fileHash;
      @XmlElement
      long offset;
      @XmlElement
      long length;
    }
  }

  private static MessageDigest getMD5Digest() {
    try {
      return MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException ignored) {
    }

    return null;
  }
}
