package com.spbau.bibaev.homework.torrent.common;

import java.util.concurrent.TimeUnit;

public class Details {
  public static final int FILE_PART_SIZE = 10 * 1024 * 1014; // 10 mb

  public static final int DEFAULT_SERVER_PORT = 8081;
  public static final int DEFAULT_CLIENT_PORT = 8082;

  public static int partCount(long size) {
    return (int) Math.ceil((double) size / FILE_PART_SIZE);
  }


  public static class Client {

    public static final long UPDATE_PERIOD_MILLIS = TimeUnit.MINUTES.toMillis(1);

    public static final int DOWNLOAD_WORKERS_COUNT = 10;

    /**
     * Receive a part of some file
     * <p>
     * Parameters:
     * {@code id} - id of a file
     * {@code partNum} - number of the part
     * <p>
     * Answer:
     * {@code count} - size of part
     * {@code bytes} - content of the part of file
     */
    public static final byte GET_REQUEST_ID = 1;

    /**
     * Receive parts which stores on the client
     * <p>
     * Parameters
     * {@code id} - id of the file
     * <p>
     * Answer:
     * {@code count} - count of parts
     * for each part on the client (totally {@code count}):
     * {@code partNum} - number of a part
     */
    public static final byte STAT_REQUEST_ID = 2;

    public static final int REQUEST_HANDLING_WORKERS = 10;
  }

  public static class Server {

    public static final int REQUEST_HANDLING_WORKERS = 4;

    public static final long TIME_TO_RELEASE_FILES_MILLIS = TimeUnit.MINUTES.toMillis(5);

    /**
     * Send to server a list of shared files
     * <p>
     * Parameters: {}
     * <p>
     * Answer:
     * {@code count} - count of known files
     * for each file in shared file (totally {@code count}):
     * {@code id} - unique identity of a file
     */
    public static final byte LIST_REQUEST_ID = 1;

    /**
     * Publish a new file
     * <p>
     * Parameters:
     * {@code String name} - name of the new file
     * {@code Long size} - size of the new file
     * <p>
     * Answer:
     * {@code Int id} - unique identity of a file
     * {@code String name} - name of the file
     * {@code Long size} - size of the file
     */
    public static final byte UPLOAD_REQUEST_ID = 2;

    /**
     * Get the list of clients which own some file
     * <p>
     * Parameters:
     * {@code Int id} - unique identity of requested a file
     * <p>
     * Answer:
     * {@code Int size} - count of clients, which contain file with id equals {@code id}
     * for each client (totally {@code size})
     * {@code Byte[4] ip} - the IP address of a client
     * {@code Short port} - the port of a client
     */
    public static final byte SOURCES_REQUEST_ID = 3;
    /**
     * Update list of files for some client
     * <p>
     * Parameters:
     * {@code Short port} - port of client
     * {@code Int count} - count of shared files in client
     * for each file (totally {@code count})
     * {@code Int id} - unique identity of a file
     * <p>
     * Answer:
     * {@code Boolean status} - status of the operation (true if successful)
     */
    public static final byte UPDATE_REQUEST_ID = 4;
  }

  private Details() {
  }
}
