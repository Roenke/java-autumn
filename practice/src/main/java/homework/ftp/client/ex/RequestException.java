package homework.ftp.client.ex;

public class RequestException extends Exception {
  public RequestException(Throwable cause) {
    super(cause);
  }

  public RequestException(String message) {
    super(message);
  }
}
