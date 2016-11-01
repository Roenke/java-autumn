package homework.ftp.server.handlers.ex;

import org.jetbrains.annotations.NotNull;

public class WrongDataFormatException extends QueryHandlerException {
  public WrongDataFormatException(@NotNull Throwable cause) {
    super(cause);
  }
}
