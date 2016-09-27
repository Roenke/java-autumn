package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.repository.api.Repository;
import com.spbau.bibaev.homework.vcs.repository.api.Revision;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class RepositoryTest extends RepositoryTestCase {
  @Test
  public void commitAll() throws IOException {
    final Repository repository = openRepository();
    String commitMessage = "Hello";
    List<Path> files = repository.getProject().getAllFiles();
    Revision revision = repository.commitChanges(commitMessage);

    assertEquals(repository.getCurrentBranch().getLastRevision().getDate(), revision.getDate());
    assertEquals(2, repository.getCurrentBranch().getRevisions().size());
    assertEquals(commitMessage, revision.getMessage());

    Path rootPath = repository.getProject().getRootDirectory();
    for(Path relativePath : repository.getCurrentBranch().getLastRevision().getFilePaths()) {
      assertTrue("File " + relativePath.toString() + " not found in project",
          files.contains(rootPath.resolve(relativePath)));
    }
  }
}
