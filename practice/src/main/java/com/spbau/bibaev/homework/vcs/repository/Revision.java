package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryIOException;
import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.util.FileState;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import com.spbau.bibaev.homework.vcs.util.Pair;
import com.spbau.bibaev.homework.vcs.util.XmlSerializer;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Revision {
  private static final String REVISION_METADATA_FILENAME = "revision_meta.xml";
  private static final String SNAPSHOT_FILENAME = "snapshot.data";
  private static final String INITIAL_COMMIT_MESSAGE = "Initial commit";
  private final String myHash;
  private final String myMessage;
  private final String myAuthor;
  private final Date myDate;
  private final Map<String, String> myFile2Hash;
  private final RevisionSnapshot mySnapshot;

  private Revision(@NotNull Revision.RevisionMetadata meta, @NotNull File snapshotFile) {
    myHash = meta.hash;
    myAuthor = meta.author;
    myMessage = meta.message;
    myFile2Hash = meta.file2Descriptor.keySet().stream()
        .collect(Collectors.toMap(Function.identity(), x -> meta.file2Descriptor.get(x).fileHash));
    myDate = meta.date;
    Map<String, Pair<Long, Long>> file2Pos = meta.file2Descriptor.keySet().stream()
        .collect(Collectors.toMap(Function.identity(),
            s -> Pair.makePair(meta.file2Descriptor.get(s).offset, meta.file2Descriptor.get(s).length)));
    mySnapshot = new RevisionSnapshot(snapshotFile, file2Pos);
  }

  static Revision read(@NotNull File dir) throws RepositoryOpeningException {
    String revisionName = dir.getName();
    File metadataFile = FilesUtil.findFileByName(dir, REVISION_METADATA_FILENAME);
    if (metadataFile == null) {
      throw new RepositoryOpeningException("Metadata file not found for revision" + revisionName);
    }

    RevisionMetadata meta;
    try {
      meta = XmlSerializer.deserialize(metadataFile, RevisionMetadata.class);
    } catch (JAXBException e) {
      throw new RepositoryOpeningException("Could not read metadata for revision " + revisionName, e);
    }

    File snapshotFile = FilesUtil.findFileByName(dir, SNAPSHOT_FILENAME);
    if (snapshotFile == null) {
      throw new RepositoryOpeningException("Snapshot file not found for revision " + revisionName);
    }
    return new Revision(meta, snapshotFile);
  }

  public String getHash() {
    return myHash;
  }

  public Date getDate() {
    return myDate;
  }

  public String getMessage() {
    return myMessage;
  }

  public String getAuthor() {
    return myAuthor;
  }

  public List<String> getAllFiles() {
    return myFile2Hash.keySet().stream().collect(Collectors.toList());
  }

  public FileState getFileState(@NotNull String path, @NotNull String hashCode) {
    if (!myFile2Hash.containsKey(path)) {
      return FileState.NEW;
    }

    String revisionHash = myFile2Hash.get(path);
    return revisionHash.equals(hashCode) ? FileState.NOT_CHANGED : FileState.MODIFIED;
  }

  static void createEmptyRevision(@NotNull File revisionDirectory, @NotNull String userName) throws RepositoryIOException {
    RevisionMetadata meta = new RevisionMetadata();
    meta.author = userName;
    meta.date = new Date();
    meta.message = INITIAL_COMMIT_MESSAGE;
    meta.hash = "";
    meta.file2Descriptor = new HashMap<>();
    Pair<File, File> files = createMetaAndSnapshotFiles(revisionDirectory);
    File metaFile = files.first;
    try {
      XmlSerializer.serialize(metaFile, RevisionMetadata.class, meta);
    } catch (JAXBException e) {
      throw new RepositoryIOException("Cannot save initial revision metadata", e);
    }
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

  private static Pair<File, File> createMetaAndSnapshotFiles(@NotNull File revisionDirectory) throws RepositoryIOException {
    File metaRevisionFile = new File(revisionDirectory.getAbsolutePath() + File.separator + REVISION_METADATA_FILENAME);
    File snapshot = new File(revisionDirectory.getAbsolutePath() + File.separator + "snapshot.data");
    try {
      if (!snapshot.createNewFile()) {
        throw new RepositoryIOException("Cannot create file for snapshot");
      }
      if (!metaRevisionFile.createNewFile()) {
        throw new RepositoryIOException("Cannot create meta file for revision");
      }
    } catch (IOException e) {
      throw new RepositoryIOException("Revision directory initializing failed", e);
    }

    return Pair.makePair(metaRevisionFile, snapshot);
  }

  public void restore(@NotNull Path tmpDirectory) throws IOException {
    mySnapshot.restore(tmpDirectory);
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

  private static class RevisionSnapshot {
    private final File myFile;
    private final Map<String, Pair<Long, Long>> myPositionMapping;

    RevisionSnapshot(@NotNull File file, @NotNull Map<String, Pair<Long, Long>> positionsMapping) {
      myFile = file;
      myPositionMapping = new HashMap<>(positionsMapping);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void restore(@NotNull Path directory) throws IOException {
      for (String pathSuffix : myPositionMapping.keySet()) {
        long offset = myPositionMapping.get(pathSuffix).first;
        long length = myPositionMapping.get(pathSuffix).second;

        File outputFile = new File(directory.toFile(), pathSuffix);
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();

        FileInputStream stream = new FileInputStream(myFile);
        stream.skip(Long.BYTES + offset);
        writeToFile(stream, outputFile, length);
      }
    }

    private void writeToFile(@NotNull InputStream in, @NotNull File file, long len) throws IOException {
      OutputStream out = new FileOutputStream(file);

      byte[] buffer = new byte[4096];
      long remain = len;
      while (remain > 0){
        int readBytes = in.read(buffer, 0, (int) Math.min(buffer.length, remain));
        out.write(readBytes);
        remain -= readBytes;
      }

      out.close();
    }
  }
}
