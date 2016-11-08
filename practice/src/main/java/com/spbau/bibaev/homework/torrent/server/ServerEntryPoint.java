package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.common.Details;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateEx;
import com.spbau.bibaev.homework.torrent.server.state.ServerStateImpl;
import com.spbau.bibaev.homework.torrent.server.state.SharedFiles;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerEntryPoint {
  private static final Logger LOG = LogManager.getLogger(ServerEntryPoint.class);

  public static void main(String[] args) {
    ArgumentParser parser = createParser();
    Namespace parsingResult;
    try {
      parsingResult = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      return;
    }

    Integer port = parsingResult.getInt("port");
    String config = parsingResult.getString("config");
    Path path = Paths.get(config);
    SharedFiles storage;
    final ObjectMapper mapper = new ObjectMapper();

    if (path.toFile().exists()) {
      try {
        LOG.info("Loading files information from: " + path.toAbsolutePath().toString());
        storage = mapper.readValue(path.toFile(), SharedFiles.class);
      } catch (IOException e) {
        LOG.error("Configuration file parsing failed. " + e.toString());
        parser.printHelp();
        return;
      }
    } else {
      LOG.info("Start with empty files information");
      storage = new SharedFiles(Collections.emptyMap());
      try {
        mapper.writeValue(path.toFile(), storage);
      } catch (IOException e) {
        LOG.fatal("Cannot save state to file " + path.toAbsolutePath());
      }
    }

    try {
      File file = path.toFile();
      if (file.exists() && (!file.isFile() || !file.canRead() || !file.canWrite())) {
        LOG.fatal("Path to configuration file should lead to regular file with read/write access");
        return;
      }
      if (!path.toFile().exists() && !path.toFile().createNewFile()) {
        LOG.fatal("Cannot create file for save state");
        return;
      }

      storage.addStateChangedListener(state -> {
        LOG.info("shared files changed");
        try {
          mapper.writerWithDefaultPrettyPrinter().writeValue(file, state);
        } catch (IOException e) {
          LOG.error("cannot save state of shared file", e);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    ServerStateEx state = new ServerStateImpl(storage);

    TorrentServer server = new TorrentServer(port, state);
    try {
      server.start();
    } catch (IOException e) {
      LOG.fatal("Something went wrong", e);
    }
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("server")
        .description("Torrent tracker server")
        .defaultHelp(true);

    parser.addArgument("-p", "--port")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_SERVER_PORT)
        .help("port for listening");

    parser.addArgument("-c", "--config")
        .type(String.class)
        .setDefault("./files-server.json")
        .help("path to json configuration file");

    return parser;
  }
}
