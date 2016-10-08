package homework.ftp.client;

import homework.ftp.client.ex.RequestException;
import homework.ftp.client.requests.GetFileRequest;
import homework.ftp.client.requests.ListFilesRequest;
import homework.ftp.common.ProtocolDetail;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ClientEntryPoint {
  private static final String ACTION_ARGUMENT_NAME = "action";
  private static final String ADDRESS_ARGUMENT_NAME = "address";
  private static final String PORT_ARGUMENT_NAME = "port";
  private static final String PATH_ARGUMENT_NAME = "name";

  public static void main(String[] args) {
    ArgumentParser parser = createParser();
    try {
      Namespace parseResult = parser.parseArgs(args);
      String action = parseResult.get(ACTION_ARGUMENT_NAME);
      int port = parseResult.get(PORT_ARGUMENT_NAME);
      Path path = parseResult.get(PATH_ARGUMENT_NAME);
      InetAddress address = parseResult.get(ADDRESS_ARGUMENT_NAME);

      Socket socket = new Socket(address, port);
      switch (action) {
        case "get":

          Path localFilePath = Paths.get(System.getProperty("user.dir")).resolve(path.getFileName());
          if(localFilePath.toFile().exists()) {
            System.err.println("Local file with such name already exists, please, move or delete it.");
            break;
          }

          Files.createFile(localFilePath);
          new GetFileRequest(path.toString(), localFilePath).execute(socket);
          System.out.println("Complete");

          break;
        case "list":
          List<ListFilesRequest.RemoteFile> requestResult = new ListFilesRequest(path.toString()).execute(socket);
          if (requestResult == null || requestResult.isEmpty()) {
            System.out.println("Directory is empty");
          } else {
            System.out.println("Content of " + path);
            requestResult.forEach(file -> System.out.println('\t' + file.getName() + (file.isDirectory() ? "/" : "")));
          }
          break;
      }
    } catch (ArgumentParserException e) {
      parser.handleError(e);
    } catch (IOException e) {
      System.err.println("Cannot open socket:" + e);
    } catch (RequestException e) {
      System.out.println(e.getMessage());
    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("client")
        .description("Yet another FTP client")
        .defaultHelp(true);

    parser.addArgument("-a", String.format("--%s", ADDRESS_ARGUMENT_NAME))
        .type(new AddressArgumentType())
        .setDefault(InetAddress.getLoopbackAddress())
        .help("the server address");

    parser.addArgument("-p", String.format("--%s", PORT_ARGUMENT_NAME))
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(ProtocolDetail.DEFAULT_PORT_NUMBER)
        .help("the server port");

    parser.addArgument(ACTION_ARGUMENT_NAME)
        .type(String.class)
        .choices("get", "list")
        .help("the command name");

    parser.addArgument(PATH_ARGUMENT_NAME)
        .type(new PathArgumentCommand())
        .setDefault(Paths.get("/"))
        .help("the path to file or directory");

    return parser;
  }

  private static class AddressArgumentType implements ArgumentType<InetAddress> {
    @Override
    public InetAddress convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
      try {
        return InetAddress.getByName(value);
      } catch (UnknownHostException e) {
        throw new ArgumentParserException(String.format("Invalid server address: %s", value), e, parser, arg);
      }
    }
  }

  private static class PathArgumentCommand implements ArgumentType<Path> {
    @Override
    public Path convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
      try {
        return Paths.get(value);
      } catch (InvalidPathException e) {
        throw new ArgumentParserException(String.format("Invalid path: %s", value), e, parser, arg);
      }
    }
  }
}
