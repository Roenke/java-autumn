package homework.ftp.server.ex;

import org.jetbrains.annotations.NonNls;

public class ServerException extends Exception {
  public ServerException(@NonNls String message) {
    super(message);
  }

  public ServerException(Throwable cause) {
    super(cause);
  }

  public ServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
