package com.spbau.bibaev.homework.torrent.client.ui.util;

import javax.swing.table.AbstractTableModel;

/**
 * @author Vitaliy.Bibaev
 */
public abstract class AbstractTableModelWithColumns extends AbstractTableModel {

  private final TableColumnDescriptor[] myColumnDescriptors;
  public AbstractTableModelWithColumns(TableColumnDescriptor[] descriptors) {
    myColumnDescriptors = descriptors;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return myColumnDescriptors[columnIndex].getColumnClass();
  }

  @Override
  public String getColumnName(int column) {
    return myColumnDescriptors[column].getName();
  }

  @Override
  public int getColumnCount() {
    return myColumnDescriptors.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return myColumnDescriptors[columnIndex].getValue(rowIndex);
  }

  public interface TableColumnDescriptor {
    Class<?> getColumnClass();
    Object getValue(int ix);
    String getName();
  }
}

