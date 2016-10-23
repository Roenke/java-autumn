package com.spbau.bibaev.homework.torrent.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbau.bibaev.homework.torrent.common.Details;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class TorrentServer {
  private static final Logger LOG = LogManager.getLogger(TorrentServer.class);
  private final int myPort;
  private final ServerState myState;

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
    ServerState state;
    if (path.toFile().exists()) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        LOG.info("Loading files information from: " + path.toAbsolutePath().toString());
        state = mapper.readValue(path.toFile(), ServerState.class);
      } catch (IOException e) {
        LOG.error("Configuration file parsing failed. " + e.toString());
        parser.printHelp();
        return;
      }
    } else {
      LOG.info("Start with empty files information");
      state = new ServerState(Collections.emptyMap());
    }

    TorrentServer server = new TorrentServer(port, state);
    server.run();
  }

  private TorrentServer(int port, @NotNull ServerState state) {
    myPort = port;
    myState = state;
  }

  private void run() {
    LOG.info("Starting the torrent server on " + myPort + " port");
    EventLoopGroup acceptor = new NioEventLoopGroup(1);
    EventLoopGroup workersGroup = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_KEEPALIVE, false)
        .group(acceptor, workersGroup)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast();
          }
        });
  }

  private static ArgumentParser createParser() {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("server")
        .description("Torrent tracker server")
        .defaultHelp(true);

    parser.addArgument("-p", "--port")
        .type(Integer.class)
        .choices(Arguments.range(0, (1 << 16) - 1))
        .setDefault(Details.DEFAULT_PORT)
        .help("port for listening");

    parser.addArgument("-c", "--config")
        .type(String.class)
        .setDefault("./files.json")
        .help("path to json configuration file");

    return parser;
  }
}
