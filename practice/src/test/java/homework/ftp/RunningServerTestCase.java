package homework.ftp;

import homework.ftp.server.FtpServer;
import homework.ftp.server.ex.ServerException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class RunningServerTestCase {
  public static final int SERVER_PORT = 32132;
  public static final String README = "README";
  public static final String README_CONTENT = "readme content";
  public static final String MAKEFILE = "Makefile";
  public static final String MAKEFILE_CONTENT = "data data data";
  public static final String DIRECTORY_NAME = "src";
  public static final String NESTED_FILE_NAME = "main.cpp";
  public static final String NESTED_FILE_CONTENT = "int main(int argc, char** argv) {}";

  @Rule
  public TemporaryFolder clientFolder = new TemporaryFolder();

  @Rule
  public TemporaryFolder serverFolder = new TemporaryFolder(){
    private volatile ServerSocket myServerSocket;
    @Override
    protected void before() throws Throwable {
      super.before();

      File readme = newFile(README);
      FileUtils.write(readme, README_CONTENT, "UTF-8");

      File makefile = newFile(MAKEFILE);
      FileUtils.write(makefile, MAKEFILE_CONTENT, "UTF-8");

      File src = newFolder(DIRECTORY_NAME);
      File main = Files.createFile(src.toPath().resolve(NESTED_FILE_NAME)).toFile();
      FileUtils.write(main, NESTED_FILE_CONTENT, "UTF-8");

      new Thread(() -> {
        FtpServer ftpServer = new FtpServer(getRoot().toPath());
        try {
          myServerSocket = new ServerSocket(SERVER_PORT);
          ftpServer.start(myServerSocket);
        } catch (IOException e) {
          System.err.println("Could not start server");
        } catch (ServerException e) {
          System.err.println("Something went wrong" + e.toString());
        }
      }).start();
    }

    @Override
    protected void after() {
      super.after();
      try {
        myServerSocket.close();
      } catch (IOException e) {
        System.err.println("Could not close the server socket");
      }
    }
  };
}
