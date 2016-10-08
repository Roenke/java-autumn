package homework.ftp.server.handlers;

import homework.ftp.server.handlers.ex.QueryHandlerException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

abstract class FtpHandler extends Handler.AbstractHandler {
  private final Path myPath;

  FtpHandler(@NotNull Socket socket, @NotNull Path path) {
    super(socket);
    myPath = path;
  }

  @Override
  public void handle(@NotNull Socket clientSocket) throws QueryHandlerException {
    try {
      handle(clientSocket, myPath);
    } catch (IOException e) {
      System.err.println("Input/output error happened: " + e.toString());
      onIoError(clientSocket, e);
    }
  }

  protected abstract void handle(@NotNull Socket socket, @NotNull Path directory)
      throws QueryHandlerException, IOException;

  protected abstract void onIoError(@NotNull Socket socket, @NotNull IOException ex);
}
