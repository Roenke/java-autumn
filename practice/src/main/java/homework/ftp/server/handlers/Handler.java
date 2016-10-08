package homework.ftp.server.handlers;

import homework.ftp.server.handlers.ex.QueryHandlerException;
import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public interface Handler extends Runnable {

  abstract class AbstractHandler implements Handler {
    private final Socket mySocket;

    public AbstractHandler(@NotNull Socket socket) {
      mySocket = socket;
    }

    @Override
    public void run() {
      try {
        handle(mySocket);
      } catch (QueryHandlerException e) {
        System.err.println("Error: " + e.toString());
      }
    }
  }

  void handle(@NotNull Socket clientSocket) throws QueryHandlerException;
}
