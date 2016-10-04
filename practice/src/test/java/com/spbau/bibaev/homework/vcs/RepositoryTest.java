package com.spbau.bibaev.homework.vcs;

import com.spbau.bibaev.homework.vcs.repository.api.v2.Commit;
import com.spbau.bibaev.homework.vcs.repository.api.v2.Repository;
import com.spbau.bibaev.homework.vcs.repository.impl.v2.RepositoryImpl;
import org.jetbrains.annotations.Nullable;
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
    Commit revision = repository.commitChanges(commitMessage);

    assertEquals(repository.getCurrentBranch().getCommit().getMeta().getDate(), revision.getMeta().getDate());
    assertEquals(commitMessage, revision.getMeta().getMessage());

    Path rootPath = repository.getProject().getRootDirectory();
//    for(Path relativePath : repository.getCurrentBranch().getLastRevision().getFilePaths()) {
//      assertTrue("File " + relativePath.toString() + " not found in project",
//          files.contains(rootPath.resolve(relativePath)));
//    }
  }

  @Test
  public void openFromSubdirectory() throws IOException {
    final Path srcDir = myRule.getRoot().toPath().resolve("src");
    final RepositoryImpl repo = RepositoryImpl.openRepository(srcDir);
    assertNotNull(repo);
    assertEquals(repo.getProject().getRootDirectory(), myRule.getRoot().toPath());
  }
}
