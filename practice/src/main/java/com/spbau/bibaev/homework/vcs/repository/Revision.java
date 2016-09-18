package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.ex.RepositoryOpeningException;
import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import com.spbau.bibaev.homework.vcs.util.XmlSerializer;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.Date;
import java.util.Map;

public class Revision {
  private static final String REVISION_METADATA_FILENAME = "revision_meta.xml";
  private static final String SNAPSHOT_FILENAME = "snapshot.data";
  private final String myHash;
  private final String myMessage;
  private final String myAuthor;
  private final Date myDate;
  private final Map<String, String> myFile2Hash;
  private final RevisionSnapshot mySnapshot;

  private Revision(@NotNull MyMetadata meta, @NotNull RevisionSnapshot snapshot) {
    myHash = meta.hash;
    myAuthor = meta.author;
    myMessage = meta.message;
    myFile2Hash = meta.file2hash;
    myDate = meta.date;
    mySnapshot = snapshot;
  }

  public static Revision read(@NotNull File dir) throws RepositoryOpeningException {
    String revisionName = dir.getName();
    File[] files = dir.listFiles((dir1, name) -> dir.isFile() && REVISION_METADATA_FILENAME.equals(name));
    if(files == null || files.length == 0) {
      throw new RepositoryOpeningException("RepositoryMetadata file not found for revision" + revisionName);
    }

    if(files.length > 1) {
      throw new RepositoryOpeningException("Ambiguous metadata file for revision " + revisionName);
    }

    File metadataFile = files[0];
    MyMetadata meta = null;
    try {
      meta = XmlSerializer.deserialize(metadataFile, MyMetadata.class);
    } catch (JAXBException e) {
      throw new RepositoryOpeningException("Could not read metadata for revision " + revisionName, e);
    }

    File snapshotFile = FilesUtil.findFileByName(dir, SNAPSHOT_FILENAME);
    if(snapshotFile == null) {
      throw new RepositoryOpeningException("Snapshot file not found for revision " + revisionName);
    }
    RevisionSnapshot snapshot = new RevisionSnapshot(snapshotFile);
    return new Revision(meta, snapshot);
  }

  String getHash() {
    return myHash;
  }
  Date getDate() {
    return myDate;
  }
  String getMessage(){
    return myMessage;
  }
  String getAuthor() {
    return myAuthor;
  }

  @XmlRootElement
  private static class MyMetadata {
    String message;
    String hash;
    String author;
    Date date;
    Map<String, String> file2hash;
  }

  private static class RevisionSnapshot {
    private final File myFile;
    RevisionSnapshot(@NotNull File file) {
      myFile = file;
    }
  }
}
