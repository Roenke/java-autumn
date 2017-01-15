package homework.ftp.server;

import homework.ftp.server.ex.ServerException;
import org.jetbrains.annotations.NotNull;

import java.net.ServerSocket;

interface Server {
  void start(@NotNull ServerSocket socket) throws ServerException;
}
