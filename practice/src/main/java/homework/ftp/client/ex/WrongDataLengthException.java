package homework.ftp.client.ex;

public class WrongDataLengthException extends RequestException{
  public WrongDataLengthException(String message) {
    super(message);
  }
}
