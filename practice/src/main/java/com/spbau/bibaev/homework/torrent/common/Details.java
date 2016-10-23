package com.spbau.bibaev.homework.torrent.common;

public class Details {
  public static int FILE_PART_SIZE = 10 * 1024 * 2014; // 10 mb
  public static int DEFAULT_PORT = 8081;

  public static class Client {

    /**
     * TODO
     */
    public static byte GET_REQUEST_ID = 1;

    /**
     * TODO
     */
    public static byte STAT_REQUEST_ID = 2;
  }

  public static class Server {

    /**
     * Send to server the list of shared files
     * <p>
     * Parameters: {}
     * Answer:
     * {@code count} - count of known files
     * for each file in shared file (totally {@code count}):
     * {@code id} - unique identity of a file
     */
    public static byte LIST_REQUEST_ID = 1;

    /**
     * Publish a new file
     * <p>
     * Parameters:
     * {@code String name} - name of the new file
     * {@code Long size} - size of the new file
     * Answer:
     * {@code Int id} - unique identity of a file
     * {@code String name} - name of the file
     * {@code Long size} - size of the file
     */
    public static byte UPLOAD_REQUEST_ID = 2;

    /**
     * Get the list of clients which own some file
     * <p>
     * Parameters:
     * {@code Int id} - unique identity of requested a file
     * Answer:
     * {@code Int size} - count of clients, which contain file with id equals {@code id}
     * for each client (totally {@code size})
     * {@code Byte[4] ip} - the IP address of a client
     * {@code Short port} - the port of a client
     */
    public static byte SOURCES_REQUEST_ID = 3;
    /**
     * Update list of files for some client
     * <p>
     * Parameters:
     * {@code Short port} - port of client
     * {@code Int count} - count of shared files in client
     * for each file (totally {@code count})
     * {@code Int id} - unique identity of a file
     * Answer:
     * {@code Boolean status} - status of the operation (true if successful)
     */
    public static byte UPDATE_REQUEST_ID = 3;
  }

  private Details() {
  }
}
