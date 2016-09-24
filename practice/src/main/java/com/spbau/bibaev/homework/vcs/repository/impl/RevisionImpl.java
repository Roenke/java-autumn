package com.spbau.bibaev.homework.vcs.repository.impl;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.repository.api.FileState;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import com.spbau.bibaev.homework.vcs.repository.api.Snapshot;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import com.spbau.bibaev.homework.vcs.util.Pair;
import com.spbau.bibaev.homework.vcs.util.XmlSerializer;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RevisionImpl implements Revision {
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

  private RevisionImpl(@NotNull Path directory, @NotNull RevisionImpl.RevisionMetadata meta, @NotNull File snapshotFile) {
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
    return myFile2Hash.values().stream().map(s -> Paths.get(s)).collect(Collectors.toList());
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
    meta.hash = "";
    meta.file2Descriptor = new HashMap<>();
    Pair<File, File> files = createMetaAndSnapshotFiles(revisionDirectory);
    File metaFile = files.first;
    try {
      XmlSerializer.serialize(metaFile, RevisionMetadata.class, meta);
    } catch (JAXBException e) {
      throw new IOException("Cannot save initial revision metadata", e);
    }

    return read(revisionDirectory);
  }

  static RevisionImpl read(@NotNull File dir) throws IOException {
    String revisionName = dir.getName();
    File metadataFile = FilesUtil.findFileByName(dir, REVISION_METADATA_FILENAME);
    if (metadataFile == null) {
      throw new IOException("Metadata file not found for revision" + revisionName);
    }

    RevisionMetadata meta;
    try {
      meta = XmlSerializer.deserialize(metadataFile, RevisionMetadata.class);
    } catch (JAXBException e) {
      throw new IOException("Could not read metadata for revision " + revisionName, e);
    }

    File snapshotFile = FilesUtil.findFileByName(dir, SNAPSHOT_FILENAME);
    if (snapshotFile == null) {
      throw new IOException("Snapshot file not found for revision " + revisionName);
    }

    return new RevisionImpl(dir.toPath(), meta, snapshotFile);
  }

  Path getDirectory() {
    return myRevisionDirectory;
  }

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
    for (Path file : project.getAllFiles()) {
      Path fileRelativePath = projectRoot.relativize(file);

    }

    return read(revisionDirectory.toFile());
  }

  static String addNewRevision(@NotNull File revisionDirectory, @NotNull String message,
                               @NotNull Date date, @NotNull String user) throws RepositoryIOException {
    RevisionMetadata meta = new RevisionMetadata();
    meta.author = user;
    meta.date = date;
    meta.message = message;

    Map<Path, String> path2Hash = new HashMap<>();
    Map<Path, Pair<Long, Long>> path2OffsetAndLength = new HashMap<>();
    Base64.Encoder encoder = Base64.getEncoder();
    try {
      MessageDigest globalDigest = MessageDigest.getInstance("MD5");
      MessageDigest fileDigest = MessageDigest.getInstance("MD5");
      Pair<File, File> files = createMetaAndSnapshotFiles(revisionDirectory);
      File metaRevisionFile = files.first;
      File snapshot = files.second;

      OutputStream snapshotStream = new DigestOutputStream(Files.newOutputStream(snapshot.toPath()), globalDigest);
      File metadataDirectory = revisionDirectory.getParentFile().getParentFile();
      File projectDirectory = metadataDirectory.getParentFile();
      Path projectPath = projectDirectory.toPath();
      AtomicLong offset = new AtomicLong(0);
      Files.walkFileTree(projectDirectory.toPath(), new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          return dir.equals(metadataDirectory.toPath())
              ? FileVisitResult.SKIP_SUBTREE
              : FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          fileDigest.reset();
          Path relativeFilePath = projectPath.relativize(file);
          InputStream inputStream = Files.newInputStream(file);
          DigestInputStream fileStream = new DigestInputStream(inputStream, fileDigest);
          long length = file.toFile().length();
          FilesUtil.copy(fileStream, snapshotStream);
          String fileHash = encoder.encodeToString(fileDigest.digest());
          path2Hash.put(relativeFilePath, fileHash);
          path2OffsetAndLength.put(relativeFilePath, Pair.makePair(offset.get(), length));
          offset.addAndGet(length);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          throw exc;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }
      });

      // Add date to end of revision
      ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
      byteBuffer.putLong(date.getTime());
      snapshotStream.write(byteBuffer.array());

      snapshotStream.close();
      String globalHash = encoder.encodeToString(globalDigest.digest());
      meta.hash = globalHash;
      meta.file2Descriptor = path2Hash.keySet().stream()
          .collect(Collectors.toMap(Path::toString,
              p -> new RevisionMetadata.FileDescriptor(path2Hash.get(p),
                  path2OffsetAndLength.get(p).first,
                  path2OffsetAndLength.get(p).second)));
      XmlSerializer.serialize(metaRevisionFile, RevisionMetadata.class, meta);
      if (!revisionDirectory.renameTo(new File(revisionDirectory.getParentFile().getAbsolutePath() + File.separator + globalHash.replace('/', '-')))) {
        throw new RepositoryIOException("Snapshot file exists");
      }
      return globalHash;
    } catch (IOException e) {
      throw new RepositoryIOException("IO exception occurred", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RepositoryIOException("Hashing algorithm not found", e);
    } catch (JAXBException e) {
      throw new RepositoryIOException("Cannot save revision metadata", e);
    }
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
}
