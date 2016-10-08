package homework.ftp.client.requests;

import homework.ftp.client.ex.RemoteAccessDeniedException;
import homework.ftp.client.ex.RemoteFileNotFoundException;
import homework.ftp.client.ex.RequestException;
import homework.ftp.common.ProtocolDetail;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ListFilesRequest extends FtpRequest<List<ListFilesRequest.RemoteFile>> {
  private final String myRemotePath;

  public ListFilesRequest(@NotNull String remotePath) {
    myRemotePath = remotePath;
  }

  public interface RemoteFile {
    String getName();
    boolean isDirectory();
  }

  @Override
  protected List<RemoteFile> executeImpl(@NotNull Socket socket) throws IOException, RequestException {
    try (DataOutputStream os = new DataOutputStream(socket.getOutputStream());
         DataInputStream is = new DataInputStream(socket.getInputStream())) {
      os.writeInt(ProtocolDetail.LIST_ACTION_ID);
      os.writeUTF(myRemotePath);

      long result = is.readLong();
      if (result < 0) {
        if (result == ProtocolDetail.ErrorCodes.SUCH_DIRECTORY_NOT_FOUND) {
          throw new RemoteFileNotFoundException(String.format("Remote directory %s not found.", myRemotePath));
        }
        if (result == ProtocolDetail.ErrorCodes.ACCEESS_DENIED) {
          throw new RemoteAccessDeniedException(String
              .format("Cannot read from remote file %s. Access denied.", myRemotePath));
        }

        throw new RequestException("Unknown error. Code = " + result);
      }

      List<RemoteFile> resultList = new ArrayList<>();
      for (long i = 0; i < result; i++) {
        String filename = is.readUTF();
        boolean isDirectory = is.readBoolean();
        resultList.add(new RemoteFileImpl(filename, isDirectory));
      }

      return resultList;
    }
  }

  private static class RemoteFileImpl implements RemoteFile {
    private final String myFilename;
    private final boolean myIsDirectory;
    RemoteFileImpl(String filename, boolean isDirectory) {
      myFilename = filename;
      myIsDirectory = isDirectory;
    }

    @Override
    public String getName() {
      return myFilename;
    }

    @Override
    public boolean isDirectory() {
      return myIsDirectory;
    }
  }
}
