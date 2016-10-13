package homework.ftp;

import homework.ftp.client.ex.RemoteFileNotFoundException;
import homework.ftp.client.ex.RequestException;
import homework.ftp.client.requests.GetFileRequest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;

public class GetRequestTest extends RunningServerTestCase {
  @Test
  public void getExistedFile() throws IOException, RequestException {
    try (Socket socket = new Socket("localhost", RunningServerTestCase.SERVER_PORT)) {
      new GetFileRequest(RunningServerTestCase.README, clientFolder.getRoot().toPath()
          .resolve(RunningServerTestCase.README)).execute(socket);
      File[] files = clientFolder.getRoot().listFiles();
      assertNotNull(files);
      assertNotEquals(0, files.length);
      String content = FileUtils.readFileToString(files[0], "UTF-8");
      assertFalse(RunningServerTestCase.DIRECTORY_NAME.isEmpty());
      assertEquals(RunningServerTestCase.README_CONTENT, content);
      socket.close();
    }
  }

  @Test(expected = RemoteFileNotFoundException.class)
  public void getAbsentFile() throws IOException, RequestException {
    try (Socket socket = new Socket("localhost", RunningServerTestCase.SERVER_PORT)) {
      new GetFileRequest(RunningServerTestCase.README + "sadad", clientFolder.getRoot().toPath()
          .resolve(RunningServerTestCase.README)).execute(socket);
    }
  }
}
