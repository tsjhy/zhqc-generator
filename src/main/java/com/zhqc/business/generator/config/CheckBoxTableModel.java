package com.zhqc.business.generator.config;

import javax.swing.table.AbstractTableModel;

public class CheckBoxTableModel extends AbstractTableModel {

    private String[] columnNames;
    private int fieldLengh;
    private Object[][] data;

    public CheckBoxTableModel(){

    }
    public CheckBoxTableModel(String[] columnNames, Object[][] data, int fieldLengh) {
        this.columnNames = columnNames;
        this.data = data;
        this.fieldLengh = fieldLengh;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }

    public int getFieldLengh() {
        return fieldLengh;
    }

    public void setFieldLengh(int fieldLengh) {
        this.fieldLengh = fieldLengh;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }


    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public void selectAllOrNull(boolean value, int columnIndex) {
        // Select All. The last column
        for (int index = 0; index < getRowCount(); index++) {
            this.setValueAt(value, index, columnIndex);
        }
    }

    public Object[] getValueAtColumn(int columnIndex) {
        int rowCount = getRowCount();
        Object[] objects = new Object[rowCount];
        for (int i = 0; i < rowCount; i++) {
            objects[i] = data[i][columnIndex];
        }
        return objects;
    }
}