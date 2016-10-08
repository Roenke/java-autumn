package homework.ftp.server.ex;

import homework.ftp.server.ex.ServerException;
import org.jetbrains.annotations.NonNls;

public class OpenSocketException extends ServerException {
  public OpenSocketException(@NonNls String message) {
    super(message);
  }

  public OpenSocketException(Throwable cause) {
    super(cause);
  }

  public OpenSocketException(String message, Throwable cause) {
    super(message, cause);
  }
}
