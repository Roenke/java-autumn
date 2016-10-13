package homework.ftp.client.requests;

import homework.ftp.client.ex.RequestException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetFileRequestTest {
  private static final String FILE = "file.txt";
  private static final String CONTENT = "content";
  @Rule
  public TemporaryFolder myRule = new TemporaryFolder(){
    @Override
    protected void before() throws Throwable {
      super.before();
      File file = newFile(FILE);
      Files.write(file.toPath(), CONTENT.getBytes());
    }
  };

  @Test
  public void simpleGetFile() throws RequestException, IOException {
    GetFileRequest request = new GetFileRequest(FILE, myRule.getRoot().toPath());
    Socket socket = mock(Socket.class);

    List<Byte> out = new ArrayList<>();
    when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(FILE.getBytes()));

    request.execute(socket);

    Byte[] res = new Byte[out.size()];
    out.toArray(res);
    String result = new String(toPrimitive(res));
  }
}
