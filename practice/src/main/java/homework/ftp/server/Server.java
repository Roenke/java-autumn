package homework.ftp.server;

import homework.ftp.server.ex.ServerException;

public interface Server {
  void start() throws ServerException;
}
