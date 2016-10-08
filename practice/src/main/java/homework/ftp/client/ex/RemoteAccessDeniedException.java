package homework.ftp.client.ex;

public class RemoteAccessDeniedException extends RequestException {
  public RemoteAccessDeniedException(String message) {
    super(message);
  }
}
