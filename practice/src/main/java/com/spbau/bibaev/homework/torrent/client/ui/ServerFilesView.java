package com.spbau.bibaev.homework.torrent.client.ui;

import com.spbau.bibaev.homework.torrent.client.ui.util.AbstractTableColumnDescriptor;
import com.spbau.bibaev.homework.torrent.client.ui.util.AbstractTableModelWithColumns;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
public class ServerFilesView extends JPanel {
  private final JTable myTable;
  private final List<FileInformation> myItems = new ArrayList<>();

  public ServerFilesView() {
    super(new BorderLayout());
    myItems.add(new FileInformation(0, new FileInfo("test", 100)));
    myTable = new JTable(new AbstractTableModelWithColumns(new AbstractTableColumnDescriptor[]{
        new AbstractTableColumnDescriptor("ID", Integer.class) {
          @Override
          public Object getValue(int ix) {
            return myItems.get(ix).id;
          }
        },
        new AbstractTableColumnDescriptor("Name", String.class) {
          @Override
          public Object getValue(int ix) {
            return myItems.get(ix).name;
          }
        },
        new AbstractTableColumnDescriptor("Size", Long.class) {
          @Override
          public Object getValue(int ix) {
            return myItems.get(ix).size;
          }
        }
    }) {
      @Override
      public int getRowCount() {
        return myItems.size();
      }
    });

    add(new JLabel("Available files on the server"), BorderLayout.NORTH);
    add(new JScrollPane(myTable), BorderLayout.CENTER);
    setVisible(true);
  }

  public void setFiles(@NotNull Map<Integer, FileInfo> files) {
    assert SwingUtilities.isEventDispatchThread();
    myItems.clear();
    files.forEach((id, info) -> myItems.add(new FileInformation(id, info)));
  }

  private static class FileInformation {
    final int id;
    final String name;
    final long size;

    FileInformation(int id, @NotNull FileInfo info) {
      super();
      this.id = id;
      name = info.getName();
      size = info.getSize();
    }
  }
}
