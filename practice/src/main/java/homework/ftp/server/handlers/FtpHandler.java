package homework.ftp.server.handlers;

import homework.ftp.server.handlers.ex.QueryHandlerException;
import homework.ftp.server.handlers.ex.WrongDataFormatException;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
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
      System.err.println("Input/output error occurred: " + e.toString());
      onIoError(clientSocket, e);
    }
  }

  protected abstract void handle(@NotNull Socket socket, @NotNull Path directory)
      throws QueryHandlerException, IOException;

  protected abstract void onIoError(@NotNull Socket socket, @NotNull IOException ex);

  String readString(@NotNull DataInputStream is) throws WrongDataFormatException, IOException {
    try {
      return is.readUTF();
    } catch (UTFDataFormatException e) {
      throw new WrongDataFormatException(e);
    }
  }
}
