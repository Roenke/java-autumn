package homework.ftp.server.handlers;

import homework.ftp.common.ProtocolDetail;
import homework.ftp.server.handlers.ex.QueryHandlerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Collection;

public class ListActionHandler extends FtpHandler {

  public ListActionHandler(@NotNull Socket socket, @NotNull Path path) {
    super(socket, path);
  }

  @Override
  protected void handle(@NotNull Socket socket, @NotNull Path directory) throws QueryHandlerException, IOException {
    try (DataInputStream is = new DataInputStream(socket.getInputStream());
         DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
      String path = is.readUTF();

      File targetFile = directory.resolve(path).toFile();
      if (!targetFile.exists() || !targetFile.isDirectory()) {
        os.writeLong(ProtocolDetail.ErrorCodes.SUCH_DIRECTORY_NOT_FOUND);
      } else {
        Collection<File> files = FileUtils
            .listFilesAndDirs(targetFile, FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter());
        long count = files.size();
        os.writeLong(count);
        for (File file : files) {
          os.writeUTF(file.getName());
          os.writeBoolean(file.isDirectory());
        }
      }
    }
  }

  @Override
  protected void onIoError(@NotNull Socket socket, @NotNull IOException ex) {

  }
}
