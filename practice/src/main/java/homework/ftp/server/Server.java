package homework.ftp.server;

import homework.ftp.server.ex.ServerException;

interface Server {
  void start() throws ServerException;
}
