package homework.ftp.server.handlers.ex;

import org.jetbrains.annotations.NotNull;

public class QueryHandlerException extends Exception {
  QueryHandlerException(@NotNull Throwable cause) {
    super(cause);
  }
}
