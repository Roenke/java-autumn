package homework.ftp.server.handlers.ex;

import org.jetbrains.annotations.NonNls;

public class WrongDataFormatException extends QueryHandlerException {
  public WrongDataFormatException(@NonNls String message) {
    super(message);
  }
}
