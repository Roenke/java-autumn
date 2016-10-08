package homework.ftp.client;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
      Path Path = parseResult.get(PATH_ARGUMENT_NAME);
      InetAddress address = parseResult.get(ADDRESS_ARGUMENT_NAME);
      switch (action) {
        case "get":
          break;
        case "list":
          break;
      }
    } catch (ArgumentParserException e) {
      parser.handleError(e);
    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("client")
        .description("Yet another ftp client")
        .defaultHelp(true);

    parser.addArgument("-a", String.format("--%s", ADDRESS_ARGUMENT_NAME))
        .type(new AddressArgumentType())
        .setDefault(InetAddress.getLoopbackAddress())
        .help("the server address");

    parser.addArgument("-p", String.format("--%s", PORT_ARGUMENT_NAME))
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(21)
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
