package homework.ftp;

import homework.ftp.client.ex.RequestException;
import homework.ftp.client.requests.ListFilesRequest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListRequestTest extends RunningServerTestCase {
  @Test
  public void listForCurrentDirectory() throws IOException, RequestException {
    final List<ListFilesRequest.RemoteFile> remoteFiles = listInternal(".");
    assertEquals(3, remoteFiles.size());
    Collection<String> expected = Arrays.asList(README, MAKEFILE, DIRECTORY_NAME);
    for (ListFilesRequest.RemoteFile file : remoteFiles) {
      assertTrue(expected.contains(file.getName()));
      if(file.getName().endsWith("/")) {
        assertTrue(file.isDirectory());
      }
    }
  }

  @Test
  public void listForNestedDirectory() throws IOException, RequestException {
    final List<ListFilesRequest.RemoteFile> remoteFiles = listInternal(RunningServerTestCase.DIRECTORY_NAME);
    assertEquals(1, remoteFiles.size());
    assertFalse(remoteFiles.get(0).isDirectory());
    assertEquals(RunningServerTestCase.NESTED_FILE_NAME, remoteFiles.get(0).getName());
  }

  private List<ListFilesRequest.RemoteFile> listInternal(String relativePath) throws IOException, RequestException {
    try (Socket socket = new Socket("localhost", RunningServerTestCase.SERVER_PORT)) {
      return new ListFilesRequest(relativePath).execute(socket);
    }
  }
}
