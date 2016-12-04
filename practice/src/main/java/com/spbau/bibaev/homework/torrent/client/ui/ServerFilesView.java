package com.spbau.bibaev.homework.torrent.client.ui;

import com.spbau.bibaev.homework.torrent.client.ui.util.AbstractTableColumnDescriptor;
import com.spbau.bibaev.homework.torrent.client.ui.util.AbstractTableModelWithColumns;
import com.spbau.bibaev.homework.torrent.client.ui.util.SizeUtil;
import com.spbau.bibaev.homework.torrent.common.FileInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vitaliy.Bibaev
 */
class ServerFilesView extends JPanel {
  private final JTable myTable;
  private final List<FileInformation> myItems = new ArrayList<>();

  ServerFilesView() {
    super(new BorderLayout());
    myTable = new JTable(new MyTableModelWithColumns());
    myTable.setDefaultRenderer(Long.class, new DefaultTableCellRenderer() {
      @Override
      protected void setValue(Object value) {
        setHorizontalAlignment(RIGHT);
        long size = (Long) value;
        setText(SizeUtil.getPrettySize(size));
      }
    });
    myTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setHorizontalAlignment(SwingConstants.CENTER);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      }
    });

    add(new JLabel("Available files on the server", SwingConstants.CENTER), BorderLayout.NORTH);
    add(new JScrollPane(myTable), BorderLayout.CENTER);
    setVisible(true);
  }

  public void setFiles(@NotNull Map<Integer, FileInfo> files) {
    assert SwingUtilities.isEventDispatchThread();
    myItems.clear();
    files.forEach((id, info) -> myItems.add(new FileInformation(id, info)));
    ((MyTableModelWithColumns) myTable.getModel()).fireTableDataChanged();
  }

  @NotNull
  List<Integer> getSelectedFileIds() {
    assert SwingUtilities.isEventDispatchThread();
    return Arrays.stream(myTable.getSelectedRows())
        .map(x -> myItems.get(myTable.convertRowIndexToModel(x)).id)
        .boxed()
        .collect(Collectors.toList());
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

  private class MyTableModelWithColumns extends AbstractTableModelWithColumns {
    MyTableModelWithColumns() {
      super(new AbstractTableColumnDescriptor[]{
          new AbstractTableColumnDescriptor("ID", Integer.class) {
            @Override
            public Object getValue(int ix) {
              return ServerFilesView.this.myItems.get(ix).id;
            }
          },
          new AbstractTableColumnDescriptor("Name", String.class) {
            @Override
            public Object getValue(int ix) {
              return ServerFilesView.this.myItems.get(ix).name;
            }
          },
          new AbstractTableColumnDescriptor("Size", Long.class) {
            @Override
            public Object getValue(int ix) {
              return ServerFilesView.this.myItems.get(ix).size;
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
