package com.spbau.bibaev.benchmark.client;

import com.spbau.bibaev.benchmark.client.ui.MainWindow;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Vitaliy.Bibaev
 */
public class ClientEntryPoint {
  private static final String ADDRESS_ARGUMENT_NAME = "address";

  public static void main(String[] args) throws IOException {
    ArgumentParser parser = createParser();
    try {
      Namespace parseResult = parser.parseArgs(args);
      InetAddress address = parseResult.get(ADDRESS_ARGUMENT_NAME);
      File log = new File("log.txt");
      final MainWindow mainWindow = new MainWindow(log, address);
      mainWindow.setVisible(true);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("client")
        .description("Benchmark client")
        .defaultHelp(true);

    parser.addArgument("-a", String.format("--%s", ADDRESS_ARGUMENT_NAME))
        .type(new AddressArgumentType())
        .setDefault(InetAddress.getLoopbackAddress())
        .help("the address of a benchmark server");

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
}
