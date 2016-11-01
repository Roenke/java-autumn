package homework.ftp.client.requests;

import homework.ftp.client.ex.RemoteAccessDeniedException;
import homework.ftp.client.ex.RemoteFileNotFoundException;
import homework.ftp.client.ex.RequestException;
import homework.ftp.client.ex.WrongDataLengthException;
import homework.ftp.common.ProtocolDetail;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class GetFileRequest extends FtpRequest<Void> {
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
        if (result == ProtocolDetail.ErrorCodes.ACCESS_DENIED) {
          throw new RemoteAccessDeniedException(myRemotePath);
        }

        throw new RequestException("Unknown remote error.");
      }

      try (OutputStream out = Files.newOutputStream(myLocalPath)) {
        long copied = IOUtils.copyLarge(is, out);
        if (copied != result) {
          throw new WrongDataLengthException(
              String.format("Length of received data not matched with expected: expect: %d, received: %d",
                  result, copied));
        }
      }
    }

    return null;
  }
}
