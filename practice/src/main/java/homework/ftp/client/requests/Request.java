package homework.ftp.client.requests;

import homework.ftp.client.ex.RequestException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;

public interface Request<R> {

  @Nullable
  R execute(@NotNull Socket socket) throws RequestException;
}
