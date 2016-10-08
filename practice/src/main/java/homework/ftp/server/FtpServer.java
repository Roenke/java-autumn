package homework.ftp.server;

import homework.ftp.server.ex.OpenSocketException;
import homework.ftp.server.ex.ServerException;
import homework.ftp.server.handlers.GetActionHandler;
import homework.ftp.server.handlers.Handler;
import homework.ftp.server.handlers.ListActionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

class FtpServer implements Server {
  private final Path myPath;
  private final int myPort;
  private final ExecutorService myThreadPool = Executors.newCachedThreadPool();
  private static final Map<Integer, BiFunction<Socket, Path, Handler>> HANDLER_SUPPLIERS = new HashMap<>();

  static {
    HANDLER_SUPPLIERS.put(1, ListActionHandler::new);
    HANDLER_SUPPLIERS.put(2, GetActionHandler::new);
  }

  FtpServer(@NotNull Path path, int port) {
    myPath = path;
    myPort = port;
  }

  @Override
  public void start() throws ServerException {
    ServerSocket socket = null;
    try {
      socket = new ServerSocket(myPort);
    } catch (IOException e) {
      throw new OpenSocketException(e);
    }

    //noinspection InfiniteLoopStatement
    while (true) {
      int actionId;
      Socket clientSocket;
      try {
        clientSocket = socket.accept();
        DataInputStream dataStream = new DataInputStream(clientSocket.getInputStream());
        actionId = dataStream.readInt();
      } catch (IOException e) {
        System.err.println("Connection failed. " + e.toString());
        continue;
      }

      if (HANDLER_SUPPLIERS.containsKey(actionId)) {
        myThreadPool.execute(HANDLER_SUPPLIERS.get(actionId).apply(clientSocket, myPath));
      } else {
        System.err.println("Protocol error: unknown action with id = " + String.valueOf(actionId));
      }
    }
  }
}
