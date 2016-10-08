package homework.ftp.server.handlers;

import homework.ftp.common.ProtocolDetail;
import homework.ftp.server.handlers.ex.QueryHandlerException;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

public class GetActionHandler extends FtpHandler {
  @Override
  protected void handle(@NotNull Socket socket, @NotNull Path directory) throws QueryHandlerException, IOException {
    try (DataInputStream is = new DataInputStream(socket.getInputStream());
         DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
      String path = is.readUTF();

      File targetFile = directory.resolve(path).toFile();
      if (!targetFile.exists() || targetFile.isDirectory()) {
        os.writeLong(ProtocolDetail.ErrorCodes.SUCH_FILE_NOT_FOUND);
      } else {
        if (!targetFile.canRead()) {
          os.writeLong(ProtocolDetail.ErrorCodes.ACCESS_DENIED);
        } else {
          long fileLength = targetFile.length();
          os.writeLong(fileLength);
          FileUtils.copyFile(targetFile, os);
        }
      }
    }
  }

  @Override
  protected void onIoError(@NotNull Socket socket, @NotNull IOException ex) {
    try {
      socket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public GetActionHandler(@NotNull Socket socket, @NotNull Path path) {
    super(socket, path);
  }
}
