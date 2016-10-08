package homework.ftp.server.handlers;

import homework.ftp.server.handlers.ex.QueryHandlerException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

public class GetActionHandler extends FtpHandler {
  @Override
  protected void handle(@NotNull Socket socket, @NotNull Path directory) throws QueryHandlerException, IOException {

  }

  @Override
  protected void onIoError(@NotNull Socket socket, @NotNull IOException ex) throws IOException {

  }

  public GetActionHandler(@NotNull Socket socket, @NotNull Path path) {
    super(socket, path);
  }
}
