package homework.ftp.common;

public class ProtocolDetail {
  public static final int LIST_ACTION_ID = 1;
  public static final int GET_ACTION_ID = 2;

  public static final int DEFAULT_PORT_NUMBER = 32456;

  public static class ErrorCodes {
    public static final long SUCH_FILE_NOT_FOUND = -1;
    public static final long SUCH_DIRECTORY_NOT_FOUND = -2;
    public static final long ACCESS_DENIED = -3;
  }
}
