package homework.ftp.client.requests;

import homework.ftp.client.ex.RemoteAccessDeniedException;
import homework.ftp.client.ex.RemoteFileNotFoundException;
import homework.ftp.client.ex.RequestException;
import homework.ftp.common.ProtocolDetail;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class GetFileRequest extends FtpRequest<Void> {
  private static final int BUFFER_SIZE = 4096;
  private final String myRemotePath;
  private final Path myLocalPath;

  public GetFileRequest(@NotNull String remotePath, @NotNull Path localPath) {
    myRemotePath = remotePath;
    myLocalPath = localPath;
  }

  @Override
  protected Void executeImpl(@NotNull Socket socket) throws IOException, RequestException {
    try (DataOutputStream os = new DataOutputStream(socket.getOutputStream());
         DataInputStream is = new DataInputStream(socket.getInputStream())) {
      os.writeInt(ProtocolDetail.GET_ACTION_ID);
      os.writeUTF(myRemotePath);

      long result = is.readLong();
      if (result < 0) {
        if (result == ProtocolDetail.ErrorCodes.SUCH_FILE_NOT_FOUND) {
          throw new RemoteFileNotFoundException(myRemotePath);
        }
        if(result == ProtocolDetail.ErrorCodes.ACCEESS_DENIED) {
          throw new RemoteAccessDeniedException(myRemotePath);
        }

        throw new RequestException("Unknown remote error.");
      }

      long remain = result;
      OutputStream out = Files.newOutputStream(myLocalPath);

      byte[] buffer = new byte[BUFFER_SIZE];
      while(remain > 0) {
        int read = is.read(buffer, 0, (int) Math.min(remain, BUFFER_SIZE));
        out.write(buffer, 0, read);
        remain -= read;
      }
    }

    return null;
  }
}
