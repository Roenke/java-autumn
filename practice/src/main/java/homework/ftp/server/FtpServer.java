package homework.ftp.server;

import homework.ftp.common.ProtocolDetail;
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

public class FtpServer implements Server {
  private final Path myPath;
  private final ExecutorService myThreadPool = Executors.newCachedThreadPool();
  private static final Map<Integer, BiFunction<Socket, Path, Handler>> HANDLER_SUPPLIERS = new HashMap<>();

  static {
    HANDLER_SUPPLIERS.put(ProtocolDetail.LIST_ACTION_ID, ListActionHandler::new);
    HANDLER_SUPPLIERS.put(ProtocolDetail.GET_ACTION_ID, GetActionHandler::new);
  }

  public FtpServer(@NotNull Path path) {
    myPath = path;
  }

  @Override
  public void start(@NotNull ServerSocket socket) {
    while (!socket.isClosed()) {
      int actionId;
      Socket clientSocket;
      try {
        clientSocket = socket.accept();
        System.out.println("Connection received");
        DataInputStream dataStream = new DataInputStream(clientSocket.getInputStream());
        actionId = dataStream.readInt();
        System.out.println("Action id = " + actionId);
      } catch (IOException e) {
        System.err.println("Something went wrong. " + e.toString());
        continue;
      }

      if (HANDLER_SUPPLIERS.containsKey(actionId)) {
        myThreadPool.execute(HANDLER_SUPPLIERS.get(actionId).apply(clientSocket, myPath));
      } else {
        try {
          socket.close();
        } catch (IOException e) {
          // practically inaccessible code
          throw new RuntimeException("Cannot close socket for " + socket.getInetAddress());
        }
        System.err.println("Protocol error: unknown action with id = " + String.valueOf(actionId));
      }
    }
  }
}
