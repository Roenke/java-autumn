package homework.ftp.server.handlers.ex;

import org.jetbrains.annotations.NonNls;

public class QueryHandlerException extends Exception {
  public QueryHandlerException(@NonNls String message) {
    super(message);
  }

  public QueryHandlerException(String message, Throwable cause) {
    super(message, cause);
  }
}
