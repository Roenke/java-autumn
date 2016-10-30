package homework.ftp.server.handlers;

import homework.ftp.server.handlers.ex.QueryHandlerException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public interface Handler extends Runnable {

  abstract class AbstractHandler implements Handler {
    private final Socket mySocket;

    AbstractHandler(@NotNull Socket socket) {
      mySocket = socket;
    }

    @Override
    public void run() {
      try (Socket socket = mySocket) {
        handle(socket);
      } catch (QueryHandlerException e) {
        System.err.println("Error: " + e.toString());
      } catch (IOException e) {
        throw new RuntimeException("Something went wrong", e);
      }
    }
  }

  void handle(@NotNull Socket clientSocket) throws QueryHandlerException;
}
