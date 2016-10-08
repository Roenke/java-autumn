package homework.ftp.common;

public class ProtocolDetail {
  public static int LIST_ACTION_ID = 1;
  public static int GET_ACTION_ID = 2;

  public static int DEFAULT_PORT_NUMBER = 32456;

  public static class ErrorCodes {
    public static long SUCH_FILE_NOT_FOUND = -1;
    public static long SUCH_DIRECTORY_NOT_FOUND = -2;
    public static long ACCEESS_DENIED = -3;
  }
}
