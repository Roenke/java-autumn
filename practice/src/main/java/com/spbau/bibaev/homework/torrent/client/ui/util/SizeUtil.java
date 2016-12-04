package com.spbau.bibaev.homework.torrent.client.ui.util;

import java.text.DecimalFormat;

/**
 * @author Vitaliy.Bibaev
 */
public class SizeUtil {
  private static final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};

  /**
   * http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
   */
  public static String getPrettySize(long size) {
    if (size <= 0) return "0";
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))
        + " " + units[digitGroups];
  }
}
