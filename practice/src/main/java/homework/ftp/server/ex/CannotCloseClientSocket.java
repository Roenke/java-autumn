package homework.ftp.server.ex;

public class CannotCloseClientSocket extends ServerException {
  public CannotCloseClientSocket(Throwable cause) {
    super(cause);
  }
}
