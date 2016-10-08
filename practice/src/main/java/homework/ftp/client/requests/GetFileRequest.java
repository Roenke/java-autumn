package homework.ftp.client.requests;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;
import java.nio.file.Path;

public class GetFileRequest implements Request<Void> {
  public GetFileRequest(@NotNull String remotePath, @NotNull Path localPath) {

  }

  @Override
  public Void execute(@NotNull Socket socket) {
    return null;
  }
}
