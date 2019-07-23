package com.zhqc.business.generator.config;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class CheckHeaderCellRenderer implements TableCellRenderer {
    CheckBoxTableModel tableModel;
    JTableHeader tableHeader;
    List<JCheckBox> headerBox = new ArrayList<>();
    public CheckHeaderCellRenderer(final JTable table) {
        this.tableModel = (CheckBoxTableModel) table.getModel();
        this.tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    // 获得选中列
                    int selectColumn = tableHeader.columnAtPoint(e.getPoint());
                    if (selectColumn >tableModel.getFieldLengh()-1) {
                        int headerBoxIndex = selectColumn-tableModel.getFieldLengh();
                        boolean value = !headerBox.get(headerBoxIndex).isSelected();
                        headerBox.get(headerBoxIndex).setSelected(value);
                        tableModel.selectAllOrNull(value,selectColumn);
                        tableHeader.repaint();
                    }
                }
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // TODO Auto-generated method stub
        String valueStr = (String) value;
        JLabel label = new JLabel(valueStr);
        label.setHorizontalAlignment(SwingConstants.CENTER); // 表头标签剧中
        JComponent component = column > tableModel.getFieldLengh()-1 ? buildCheckBox(column) : label;
        component.setForeground(tableHeader.getForeground());
        component.setBackground(tableHeader.getBackground());
        component.setFont(tableHeader.getFont());
        component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        return component;
    }
    private JCheckBox buildCheckBox(int column){
        if(headerBox.size() == tableModel.getColumnCount()-tableModel.getFieldLengh()){
            return headerBox.get(column-tableModel.getFieldLengh());
        }else{
            JCheckBox selectBox = new JCheckBox(tableModel.getColumnName(column));
            selectBox.setSelected(false);
            selectBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签剧中
            selectBox.setBorderPainted(true);
            headerBox.add(selectBox);
            return selectBox;
        }
    }
}
