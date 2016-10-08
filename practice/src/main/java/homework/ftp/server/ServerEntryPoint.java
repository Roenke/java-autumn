package homework.ftp.server;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerEntryPoint {
  private static final String DIRECTORY_ARGUMENT_NAME = "dir";
  private static final String PORT_ARGUMENT_NAME = "port";

  public static void main(String[] args) {
    ArgumentParser parser = createParser();
    try {
      Namespace parseResult = parser.parseArgs(args);
      Path directory = parseResult.get(DIRECTORY_ARGUMENT_NAME);
      int port = parseResult.get(PORT_ARGUMENT_NAME);
      System.out.println(directory.toAbsolutePath());
      System.out.println(port);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("server")
        .description("Yet another FTP server")
        .defaultHelp(true);

    parser.addArgument("-d", String.format("--%s", DIRECTORY_ARGUMENT_NAME))
        .type(new PathArgumentType())
        .setDefault(new File(".").toPath().toAbsolutePath())
        .help("specify the root directory of FTP server");

    parser.addArgument("-p", String.format("--%s", PORT_ARGUMENT_NAME))
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(21)
        .help("specify the port for connections");

    return parser;
  }

  private static class PathArgumentType implements ArgumentType<Path> {
    @Override
    public Path convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
      try {
        Path path = Paths.get(value);
        if (!path.toFile().exists()) {
          throw new ArgumentParserException(String.format("Path %s does not exists", value), parser, arg);
        }

        if(!path.toFile().canRead()) {
          throw new ArgumentParserException(String.format("Access denied to %s", value), parser, arg);
        }

        return path;
      } catch (InvalidPathException e) {
        throw new ArgumentParserException(String.format("Invalid path: %s", value), e, parser, arg);
      }
    }
  }

}
