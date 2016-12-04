package com.spbau.bibaev.homework.torrent.client.ui;

import com.spbau.bibaev.homework.torrent.client.api.ClientFileInfo;
import com.spbau.bibaev.homework.torrent.client.ui.util.AbstractTableColumnDescriptor;
import com.spbau.bibaev.homework.torrent.client.ui.util.AbstractTableModelWithColumns;
import com.spbau.bibaev.homework.torrent.common.Details;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy.Bibaev
 */
class LocalFilesView extends JPanel {
  private final JTable myTable;
  private final List<FileInformation> myItems = new ArrayList<>();

  LocalFilesView() {
    super(new BorderLayout());
    myItems.add(new FileInformation(10, "C:/file/test", 1000, 100, 10));
    myTable = new JTable(new MyTableModelWithColumns());
    add(new JLabel("Loaded files and progress", SwingConstants.CENTER), BorderLayout.NORTH);
    add(new JScrollPane(myTable), BorderLayout.CENTER);
  }

  void addFile(@NotNull String path, @NotNull ClientFileInfo info) {
    assert SwingUtilities.isEventDispatchThread();

    myItems.add(new FileInformation(info.getId(), path, info.getSize(),
        Details.partCount(info.getSize()), info.getParts().size()));
//    myTable.getRowSorter().allRowsChanged();
  }

  private static class FileInformation {
    final int id;
    final String path;
    final long size;
    final int totalParts;
    final int loadedParts;

    private FileInformation(int id, String path, long size, int totalParts, int loadedParts) {
      this.id = id;
      this.path = path;
      this.size = size;
      this.totalParts = totalParts;
      this.loadedParts = loadedParts;
    }
  }

  private class MyTableModelWithColumns extends AbstractTableModelWithColumns {
    MyTableModelWithColumns() {
      super(new AbstractTableColumnDescriptor[]{
          new AbstractTableColumnDescriptor("ID", Integer.class) {
            @Override
            public Object getValue(int ix) {
              return LocalFilesView.this.myItems.get(ix).id;
            }
          },
          new AbstractTableColumnDescriptor("Path", String.class) {
            @Override
            public Object getValue(int ix) {
              return LocalFilesView.this.myItems.get(ix).path;
            }
          },
          new AbstractTableColumnDescriptor("Size", Long.class) {
            @Override
            public Object getValue(int ix) {
              return LocalFilesView.this.myItems.get(ix).size;
            }
          },
          new AbstractTableColumnDescriptor("Progress", Double.class) {
            @Override
            public Object getValue(int ix) {
              FileInformation info = LocalFilesView.this.myItems.get(ix);
              return (double) info.loadedParts / info.totalParts;
            }
          }
      });
    }

    @Override
    public int getRowCount() {
      return myItems.size();
    }
  }
}
