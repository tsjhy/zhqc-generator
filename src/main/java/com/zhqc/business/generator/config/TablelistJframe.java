package com.zhqc.business.generator.config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TablelistJframe extends JFrame {

    private JPanel contentPane;
    private JList list;
    private MainFrame parentFrame;
    private List<String> tableNames;

    public Set<Integer> getSelectIndexs() {
        return selectIndexs;
    }

    private Set<Integer> selectIndexs = new HashSet<>();
    /**
     * Create the frame.
     */
    public TablelistJframe(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setSize(500, 500);
        setTitle("请勾选表");
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(50, 50, 400, 300);
        contentPane.add(scrollPane);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton cancelBtn = new JButton();
        cancelBtn.setText("取消");
        panel.add(cancelBtn);
        JButton confirmBtn = new JButton();
        confirmBtn.setText("确定");
        panel.add(confirmBtn);
        panel.setBounds(170,380,150,100);
        contentPane.add(panel);
        list = new JList();
        scrollPane.setViewportView(list);
        MyJcheckBox cell = new MyJcheckBox();
        list.setCellRenderer(cell);
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                    selectIndexs.remove(index0);
                } else {
                    super.addSelectionInterval(index0, index1);
                    selectIndexs.add(index0);
                }
            }
        });

        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.getTableBox().removeAllItems();
                for(Integer index : selectIndexs){
                    parentFrame.getTableBox().addItem(tableNames.get(index));
                }
                if(selectIndexs.size() == 0){
                    parentFrame.clearTab();
                }
                close();
            }
        });
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
    }

    public void setSelectedList(List<Integer> selectedIndexs){
        for(Integer index : selectedIndexs){
            this.list.setSelectedIndex(index);
        }
    }
    private void close(){
        setParentUser();
        this.setVisible(false);
    }
    private void setParentUser(){
        if(parentFrame != null){
            parentFrame.setEnabled(true);
        }
    }
    public void setListData(List listData){
        if(listData != null){
            tableNames = listData;
            list.setListData(listData.toArray());
        }
    }

    public void clearSelectedIndex() {
        this.selectIndexs.clear();
    }
}