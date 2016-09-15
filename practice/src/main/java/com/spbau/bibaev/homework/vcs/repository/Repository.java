package com.spbau.bibaev.homework.vcs.repository;

import com.spbau.bibaev.homework.vcs.util.FilesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class Repository {
  private static final String VCS_DIRECTORY_NAME = ".my_vcs";
  private static final String METADATA_FILENAME = "metadata.xml";
  private final File myRepositoryDirectory;
  private final File myRepositoryMetadataDirectory;
  private File myRepositoryMetadataFile;
  private Metadata myMetadata;

  /**
   * Open nearest repository (may be in parent folders) or open non-initialized here
   *
   * @param directory Start directory
   * @return Existed repository, if it exists, otherwise empty non-initialized repository
   */
  @NotNull
  public static Repository open(@NotNull File directory) {
    File currentDirectory = directory;
    while (currentDirectory != null && !FilesUtil.isContainsDirectory(currentDirectory, VCS_DIRECTORY_NAME)) {
      currentDirectory = currentDirectory.getParentFile();
    }

    File vcsRoot = currentDirectory == null ? directory : currentDirectory;
    return openHere(vcsRoot);
  }

  /**
   * Open/create repository here
   *
   * @param directory directory for repository creating
   * @return repository
   */
  public static Repository openHere(@NotNull File directory) {
    return new Repository(directory);
  }

  private Repository(@NotNull File directory) {
    myRepositoryDirectory = directory;
    File metadataDirectory = FilesUtil.findDirectoryByName(directory, VCS_DIRECTORY_NAME);
    FilesUtil.findDirectoryByName(directory, VCS_DIRECTORY_NAME);

    if (metadataDirectory == null) {
      metadataDirectory = new File(directory.getAbsolutePath() + File.separator + VCS_DIRECTORY_NAME);
    }

    myRepositoryMetadataDirectory = metadataDirectory;

    myRepositoryMetadataFile = FilesUtil.findFileByName(myRepositoryMetadataDirectory, METADATA_FILENAME);
    if (myRepositoryMetadataFile != null) {
      myMetadata = readMetadata(myRepositoryMetadataFile);
    } else {
      myRepositoryMetadataFile = new File(myRepositoryMetadataDirectory.getAbsolutePath() +
          File.separator + METADATA_FILENAME);
    }
  }

  @Nullable
  public Metadata getMetadata() {
    return myMetadata == null ? null : new Metadata(myMetadata);
  }

  /**
   * Check that metadata created
   *
   * @return true, if metadata already created, false otherwise
   */
  public boolean isInitialized() {
    return myRepositoryMetadataFile != null && myRepositoryMetadataFile.exists();
  }

  /**
   * Create metadata files for current directory
   *
   * @return true, if metadata successfully created, false if it already exists
   */
  public boolean initialize() {
    if (isInitialized()) {
      return false;
    }

    //noinspection ResultOfMethodCallIgnored
    myRepositoryMetadataDirectory.mkdir();
    try {
      //noinspection ResultOfMethodCallIgnored
      myRepositoryMetadataFile.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    myMetadata = Metadata.defaultMeta();
    saveMetadata(myRepositoryMetadataFile);
    return true;
  }

  public void saveAll() {
    if (myMetadata != null && myRepositoryMetadataFile != null && myRepositoryMetadataFile.exists()) {
      saveMetadata(myRepositoryMetadataFile);
    }
  }

  public void setMetadata(@NotNull Metadata metadata) {
    myMetadata = new Metadata(metadata);
  }

  @Nullable
  private Metadata readMetadata(@NotNull File fileToRead) {
    Metadata result = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      result = (Metadata) jaxbUnmarshaller.unmarshal(fileToRead);
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    return result;
  }

  private void saveMetadata(@NotNull File fileToSave) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
      final Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(myMetadata, fileToSave);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  private class Storage {
  }
}
