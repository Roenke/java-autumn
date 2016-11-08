package com.spbau.bibaev.homework.torrent.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.client.api.ClientStateEx;
import com.spbau.bibaev.homework.torrent.client.impl.ClientStateImpl;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.server.TorrentServer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class TorrentClient {
  private static final Logger LOG = LogManager.getLogger(TorrentServer.class);

  private final InetAddress myServerAddress;
  private final int myServerPort;
  private final int myClientPort;
  private final ClientStateEx myState;

  public static void main(String[] args) {
    final ArgumentParser parser = createParser();
    Namespace parsingResult;
    try {
      parsingResult = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      return;
    }

    final String serverAddress = parsingResult.getString("address");
    final int serverPort = parsingResult.getInt("port");
    final int clientPort = parsingResult.getInt("listen");
    final Path configPath = Paths.get(parsingResult.getString("config"));

    ClientStateEx state;
    if (configPath.toFile().exists()) {
      final ObjectMapper mapper = new ObjectMapper();
      try {
        state = mapper.readValue(configPath.toFile(), ClientStateImpl.class);
      } catch (IOException e) {
        LOG.error("Parsing of configuration file failed. " + e.toString());
        parser.printHelp();
        return;
      }
    } else {
      LOG.info("Start with empty files information");
      state = new ClientStateImpl(Collections.emptyMap());
    }

    InetAddress address;
    try {
      address = InetAddress.getByName(serverAddress);
    } catch (UnknownHostException e) {
      LOG.error("Server with this address not found: " + serverAddress);
      LOG.fatal(e);
      parser.printHelp();
      return;
    }

    TorrentClient client = new TorrentClient(clientPort, serverPort, address, state);

    try {
      client.run();
    } catch (IOException e) {
      LOG.fatal("Something went wrong", e);
    }
  }

  private TorrentClient(int listenPort, int serverPort, @NotNull InetAddress serverAddress,
                        @NotNull ClientStateEx state) {
    myClientPort = listenPort;
    myServerPort = serverPort;
    myServerAddress = serverAddress;
    myState = state;
  }

  public void run() throws IOException {
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("client")
        .description("Torrent client")
        .defaultHelp(true);

    parser.addArgument("-a", "--address")
        .type(String.class)
        .setDefault("localhost")
        .help("port for listening");

    parser.addArgument("-p", "--port")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_SERVER_PORT)
        .help("server port for connection");

    parser.addArgument("-l", "--listen")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_CLIENT_PORT)
        .help("port for listening");

    parser.addArgument("-c", "--config")
        .type(String.class)
        .setDefault("./files.json")
        .help("path to json configuration file");

    return parser;
  }
}
