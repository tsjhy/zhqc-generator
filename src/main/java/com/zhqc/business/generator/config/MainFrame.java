package com.zhqc.business.generator.config;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.zhqc.business.generator.model.GeneratorConfig;
import com.zhqc.business.generator.model.ao.TableConfigReq;
import com.zhqc.business.generator.utils.*;
import com.zhqc.framerwork.common.exception.ZhqcException;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.config.Context;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel rootPanel;
    private JTextField dbIpField;
    private JTextField portField;
    private JTextField userNameField;
    private JTextField databaseField;
    private JTextField clientIdField;
    private JPasswordField pwdField;
    private JTextField outPathField;
    private JButton selectPathBtn;
    private JTextField packageField;
    private JPanel panel1;

    private JLabel appLabel;
    private JLabel outLabel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel databaseNameLabel;
    private JLabel userNameLabel;
    private JLabel pwdLabel;
    private TablelistJframe tableFrame;
    private Context context;
    private JLabel packageLabel;
    private JTabbedPane tablePanel;
    private JPanel baseTab;
    private JLabel authorLabel;
    private JTextField authorField;
    private JPanel addTab;
    private JPanel editTab;
    private JPanel queryTab;
    private JPanel voTab;
    private JPanel footPanel;
    private JButton submitBtn;
    private JPanel panel11;
    private JButton selectTableBtn;
    private JPanel basePanel;
    private JComboBox tableBox;
    private JTextField domainField;
    private JLabel domainLabel;
    private JLabel primaryLabel;
    private JTextField primaryField;
    private JCheckBox generatorKey;
    private JPanel packagePanel;
    private JLabel pStart;
    private JTabbedPane configPanel;
    private JPanel tableTab;
    private JPanel sqlTab;
    private JScrollPane sqlScroll;
    private JTextArea sqlArea;
    private JButton parseBtn;
    private JLabel sqlLabel;
    private JTabbedPane sqlConfigTab;
    private JPanel sqlConfigPanel;
    private JPanel sqlBaseTab;
    private JPanel sqlVoTab;
    private JPanel sqlQueryTab;
    private JPanel sqlExportTab;
    private JTextField sqlDomainField;
    private JLabel sqlDomainLabel;
    private JPanel exportTab;
    private JCheckBox showExportLabel;
    private boolean parseSql = false;

    public MainFrame() {
        setTitle("智汇奇策代码生成器V1.0");
        setContentPane(rootPanel);
        tableFrame = new TablelistJframe(this);
        tableFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setParentEnabled(true);
                tableFrame.clearSelectedIndex();
            }
        });
        tablePanel.remove(5);
        tableFrame.setResizable(false);

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//设置只能选择目录
        selectPathBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show file chooser dialog
                int result = chooser.showOpenDialog(null);
                //if file selected,set it as icon of the label
                if (result == JFileChooser.APPROVE_OPTION) {
                    String name = chooser.getSelectedFile().getPath();
                    outPathField.setText(name);
                }
            }
        });
        selectTableBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTableFrame();
            }
        });
        tableBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    clearTab();
                    String dbName = databaseField.getText();
                    String tableName = e.getItem().toString();
                    List<Map<String, Object>> columns = TableUtils.getTableColumnsByTableName(context, dbName, tableName);
                    primaryField.setText(DataUtils.getPkField(tableName));
                    generatorKey.setSelected(DataUtils.getUseGeneratedKey(tableName));
                    showExportLabel.setSelected(DataUtils.getShowExport(tableName));
                    createAddTab(columns, tableName);
                    createEditTab(columns, tableName);
                    createQueryTab(columns, tableName);
                    createVOTab(columns, tableName);
                    createExportTab(columns, tableName);
                    String domainName = DataUtils.getDomainName(tableName);
                    if (StringUtils.isNotBlank(domainName)) {
                        domainField.setText(domainName);
                    }
                    showExportTab();
                    tablePanel.setSelectedIndex(0);
                }
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    buildTableConfigReq(e.getItem().toString());
                }
            }
        });
        tablePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex > 0) {
                    String domainName = domainField.getText();
                    if (StringUtils.isBlank(domainName)) {
                        WarnDialog.showWarnDialog("请填写实体名称", null);
                        tablePanel.setSelectedIndex(0);
                    }
                }
                /**
                 if(selectedIndex==0)
                 JOptionPane.showMessageDialog(null,"you suck");
                 */
            }
        });
        sqlConfigTab.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex > 0) {
                    String domainName = sqlDomainField.getText();
                    if (StringUtils.isBlank(domainName)) {
                        WarnDialog.showWarnDialog("请填写实体名称", null);
                        sqlConfigTab.setSelectedIndex(0);
                    }
                }
            }
        });
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (tableFrame.getSelectIndexs().size() == 0 && !parseSql) {
                        throw new ZhqcException(300, "请选择按表或按SQL进行配置");
                    }
                    if (tableFrame.getSelectIndexs().size() > 0) {
                        buildTableConfigReq(null);
                    }
                    if (parseSql) {
                        buildTableConfigForSql();
                    }
                    validateField();
                } catch (ZhqcException e1) {
                    WarnDialog.showWarnDialog(e1.getContent(), null);
                    return;
                }
                try {
                    initContext();

                    List<String> tables = getSelectedTables();
                    if (tables != null && tables.size() > 0) {
                        GeneratorUtils.generator(context, tables);
                    }
                    if (parseSql) {
                        GeneratorUtils.generatorBySql(context, TableUtils.getSqlTable());
                    }
                    WarnDialog.showInfoDialog("执行成功", null);
                } catch (ZhqcException e1) {
                    WarnDialog.showWarnDialog(e1.getContent(), null);
                }
            }
        });
        parseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseSql();
            }
        });
        showExportLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExportTab();
            }
        });
    }

    private void showExportTab() {
        if (showExportLabel.isSelected()) {
            tablePanel.addTab("导出配置", exportTab);
        } else {
            tablePanel.remove(exportTab);
        }
    }

    private void parseSql() {
        String sql = sqlArea.getText();
        if (StringUtils.isBlank(sql)) {
            WarnDialog.showWarnDialog("请填写SQL", this);
            return;
        }
        try {
            initContext();
        } catch (ZhqcException e1) {
            WarnDialog.showWarnDialog(e1.getContent(), null);
            return;
        }
        clearSqlTab();
        String dbName = databaseField.getText();
        try {
            List<Map<String, Object>> columns = TableUtils.parseSql(context, dbName, sql);
            createSqlVoTab(columns);
            createSqlQueryTab(columns);
            createSqlExportTab(columns);
            parseSql = true;
            WarnDialog.showInfoDialog("解析成功", this);
        } catch (ZhqcException e) {
            WarnDialog.showWarnDialog(e.getContent(), this);
        }
    }

    private void createSqlVoTab(List<Map<String, Object>> columns) {
        columns = TableUtils.buildColumns(columns, VOTableUtils.getColumnNames().length - VOTableUtils.commonLength);
        Object[][] addTabData = TableUtils.listToArray(columns, VOTableUtils.getColumnNames().length);
        createTab(sqlVoTab, VOTableUtils.getColumnNames(), addTabData, VOTableUtils.commonLength);
    }

    private void createSqlQueryTab(List<Map<String, Object>> columns) {
        columns = QueryTableUtils.buildQueryColumns(columns);
        columns = TableUtils.buildColumns(columns, QueryTableUtils.getColumnNames().length - QueryTableUtils.commonLength);
        Object[][] addTabData = TableUtils.listToArray(columns, QueryTableUtils.getColumnNames().length);
        createTab(sqlQueryTab, QueryTableUtils.getColumnNames(), addTabData, QueryTableUtils.commonLength);
    }

    private void createSqlExportTab(List<Map<String, Object>> columns) {
        columns = TableUtils.buildColumns(columns, ExportTableUtils.getColumnNames().length - ExportTableUtils.commonLength);
        Object[][] addTabData = TableUtils.listToArray(columns, ExportTableUtils.getColumnNames().length);
        createTab(sqlExportTab, ExportTableUtils.getColumnNames(), addTabData, ExportTableUtils.commonLength);
    }

    private void validateField() {
        String basePackage = packageField.getText();
        String baseOutPath = outPathField.getText();
        if (StringUtils.isBlank(basePackage)) {
            throw new ZhqcException(300, "请填写公共包名");
        }
        if (StringUtils.isBlank(baseOutPath)) {
            throw new ZhqcException(300, "请填写文件输出路径");
        }
        List<String> tables = getSelectedTables();
        for (String tableName : tables) {
            String domainName = DataUtils.getDomainName(tableName);
            if (StringUtils.isBlank(domainName)) {
                throw new ZhqcException(300, "请填写%s表的实体名称", tableName);
            }
        }
        if (parseSql) {
            String tableName = TableUtils.getSqlTable();
            if (StringUtils.isBlank(tableName)) {
                throw new ZhqcException(300, "按SQL配置:当前SQL中没有检查的表名");
            }
            TableConfigReq req = DataUtils.getTableConfigReq(tableName);
            if (req == null) {
                throw new ZhqcException(300, "按SQL配置:没有找到可用的表信息");
            }
            if (StringUtils.isBlank(req.getVoRequiredField())) {
                throw new ZhqcException(300, "按SQL配置：请选择要生成的VO属性");
            }
            if (StringUtils.isBlank(req.getBoRequiredField())) {
                throw new ZhqcException(300, "按SQL配置：请选择要生成的BO属性");
            }
            if (StringUtils.isBlank(req.getQueryPageField())) {
                throw new ZhqcException(300, "按SQL配置：请选择要显示的查询字段");
            }
            if (StringUtils.isBlank(req.getExportField())) {
                throw new ZhqcException(300, "按SQL配置：请选择要导出的字段");
            }
        }
    }

    private void buildTableConfigForSql() {
        String sql = sqlArea.getText();
        String domainName = sqlDomainField.getText();
        TableConfigReq req = DataUtils.getTableConfigReq(TableUtils.getSqlTable());
        req.setTableName(TableUtils.getSqlTable().replace("_sql", ""));
        req.setDomainName(domainName);
        req.setExportSql(sql);
        req.setShowExport(true);
        req.setGeneratorType("sql");
        CheckBoxTableModel voTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) sqlVoTab.getComponent(0)).getViewport().getView()).getModel();
        req.setVoRequiredField(getSelectedField(voTableModel, 3));
        req.setBoRequiredField(getSelectedField(voTableModel, 4));
        req.setPoRequiredField(getAllField(voTableModel));
        req.setPoFieldType(getAllFieldType(voTableModel));
        req.setPoFieldRemark(getAllFieldRemark(voTableModel));
        CheckBoxTableModel queryTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) sqlQueryTab.getComponent(0)).getViewport().getView()).getModel();
        req.setQueryPageField(getSelectedField(queryTableModel, 4));

        String queryField = getSelectedField(queryTableModel, 5);
        req.setQueryRequiredField(queryField);
        if (StringUtils.isNotBlank(queryField)) {
            req.setQueryKey(buildQueryKey(queryTableModel, queryField));
        }
        req.setQueryHiddenApiField(getSelectedField(queryTableModel, 6));
        req.setQueryHiddenValidateField(getSelectedField(queryTableModel, 7));

        CheckBoxTableModel exportTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) sqlExportTab.getComponent(0)).getViewport().getView()).getModel();
        req.setExportField(getSelectedField(exportTableModel, 3));
        req.setExportName(getSelectedRemark(exportTableModel, 3));
    }

    private void buildTableConfigReq(String tableName) {

        String currentTable;
        if (StringUtils.isNotBlank(tableName)) {
            currentTable = tableName;
        } else {
            currentTable = tableBox.getSelectedItem().toString();
        }
        String currentDomainName = domainField.getText();
        TableConfigReq req = DataUtils.getTableConfigReq(currentTable);
        req.setUseGeneratedKeys(generatorKey.isSelected());
        req.setShowExport(showExportLabel.isSelected());
        req.setDomainName(currentDomainName);
        req.setTableName(currentTable);
        req.setGeneratorType("table");
        CheckBoxTableModel voTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) voTab.getComponent(0)).getViewport().getView()).getModel();
        req.setVoRequiredField(getSelectedField(voTableModel, 3));
        req.setBoRequiredField(getSelectedField(voTableModel, 4));

        CheckBoxTableModel queryTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) queryTab.getComponent(0)).getViewport().getView()).getModel();
        req.setQueryPageField(getSelectedField(queryTableModel, 4));

        String queryField = getSelectedField(queryTableModel, 5);
        req.setQueryRequiredField(queryField);
        if (StringUtils.isNotBlank(queryField)) {
            req.setQueryKey(buildQueryKey(queryTableModel, queryField));
        }
        req.setQueryHiddenApiField(getSelectedField(queryTableModel, 6));
        req.setQueryHiddenValidateField(getSelectedField(queryTableModel, 7));

        CheckBoxTableModel addTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) addTab.getComponent(0)).getViewport().getView()).getModel();
        req.setAddRequiredField(getSelectedField(addTableModel, 3));
        req.setAddHiddenApiField(getSelectedField(addTableModel, 4));
        req.setAddHiddenValidateField(getSelectedField(addTableModel, 5));
        req.setPrimaryKey(getSelectedField(addTableModel, 6));

        CheckBoxTableModel editTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) editTab.getComponent(0)).getViewport().getView()).getModel();
        req.setEditRequiredField(getSelectedField(editTableModel, 3));
        req.setEditHiddenApiField(getSelectedField(editTableModel, 4));
        req.setEditHiddenValidateField(getSelectedField(editTableModel, 5));

        CheckBoxTableModel exportTableModel = (CheckBoxTableModel) ((JTable) ((JScrollPane) exportTab.getComponent(0)).getViewport().getView()).getModel();
        req.setExportField(getSelectedField(exportTableModel, 3));
        req.setExportName(getSelectedRemark(exportTableModel, 3));
    }

    private String buildQueryKey(CheckBoxTableModel tableModel, String queryField) {
        String[] str = queryField.split("\\,");
        Object[] temp = tableModel.getValueAtColumn(0);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < str.length; j++) {
                if (temp[i].equals(str[j])) {
                    builder.append(str[j])
                            .append("&")
                            .append(QueryTableUtils.convertCondition((String) tableModel.getValueAt(i, 3)))
                            .append("&")
                            .append(tableModel.getValueAt(i, 1))
                            .append(",");
                }
            }
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    private List<Integer> getSelectedIndex(CheckBoxTableModel tableModel, int columnIndex) {
        Object[] temp = tableModel.getValueAtColumn(columnIndex);
        List<Integer> selectedIndex = new ArrayList<>();
        if (temp.length > 0) {
            for (int i = 0; i < temp.length; i++) {
                boolean selecetd = (boolean) temp[i];
                if (selecetd) {
                    selectedIndex.add(i);
                }
            }
        }
        return selectedIndex;
    }

    private String getSelectedField(CheckBoxTableModel tableModel, int columnIndex) {
        List<Integer> selectedIndex = getSelectedIndex(tableModel, columnIndex);
        StringBuilder builder = new StringBuilder();
        if (selectedIndex.size() > 0) {
            for (Integer index : selectedIndex) {
                builder.append(tableModel.getValueAt(index, 0)).append(",");
            }
            return builder.toString().substring(0, builder.toString().length() - 1);
        }
        return null;
    }

    private String getAllField(CheckBoxTableModel tableModel) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            builder.append(tableModel.getValueAt(i, 0)).append(",");
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    private String getAllFieldType(CheckBoxTableModel tableModel) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            builder.append(tableModel.getValueAt(i, 1)).append(",");
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    private String getAllFieldRemark(CheckBoxTableModel tableModel) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            builder.append(tableModel.getValueAt(i, 2)).append(",");
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    private String getSelectedRemark(CheckBoxTableModel tableModel, int columnIndex) {
        List<Integer> selectedIndex = getSelectedIndex(tableModel, columnIndex);
        StringBuilder builder = new StringBuilder();
        if (selectedIndex.size() > 0) {
            for (Integer index : selectedIndex) {
                builder.append(tableModel.getValueAt(index, 2)).append(",");
            }
            return builder.toString().substring(0, builder.toString().length() - 1);
        }
        return null;
    }

    private void createAddTab(List<Map<String, Object>> columns, String tableName) {
        columns = TableUtils.buildColumns(columns, AddTableUtils.getColumnNames().length - AddTableUtils.commonLength);
        Object[][] addTabData = TableUtils.listToArray(columns, AddTableUtils.getColumnNames().length);
        DataUtils.saveAddTabData(addTabData, tableName);
        createTab(addTab, AddTableUtils.getColumnNames(), addTabData, AddTableUtils.commonLength);
    }

    private void createEditTab(List<Map<String, Object>> columns, String tableName) {
        columns = TableUtils.buildColumns(columns, EditTableUtils.getColumnNames().length - EditTableUtils.commonLength);
        Object[][] editTabData = TableUtils.listToArray(columns, EditTableUtils.getColumnNames().length);
        DataUtils.saveEditTabData(editTabData, tableName);
        createTab(editTab, EditTableUtils.getColumnNames(), editTabData, EditTableUtils.commonLength);
    }

    private void createQueryTab(List<Map<String, Object>> columns, String tableName) {
        columns = QueryTableUtils.buildQueryColumns(columns);
        Object[][] queryData = TableUtils.listToArray(columns, EditTableUtils.getColumnNames().length);
        DataUtils.saveQueryTabData(queryData, tableName);
        createTab(queryTab, QueryTableUtils.getColumnNames(), queryData, QueryTableUtils.commonLength);
    }

    private void createExportTab(List<Map<String, Object>> columns, String tableName) {
        columns = TableUtils.buildColumns(columns, ExportTableUtils.getColumnNames().length - ExportTableUtils.commonLength);
        Object[][] exportData = TableUtils.listToArray(columns, ExportTableUtils.getColumnNames().length);
        DataUtils.saveExportTabData(exportData, tableName);
        createTab(exportTab, ExportTableUtils.getColumnNames(), exportData, ExportTableUtils.commonLength);
    }

    private void createVOTab(List<Map<String, Object>> columns, String tableName) {
        columns = TableUtils.buildColumns(columns, VOTableUtils.getColumnNames().length - VOTableUtils.commonLength);
        Object[][] voData = TableUtils.listToArray(columns, EditTableUtils.getColumnNames().length);
        DataUtils.saveVoTabData(voData, tableName);
        createTab(voTab, VOTableUtils.getColumnNames(), voData, VOTableUtils.commonLength);
    }

    private void createTab(JPanel panel, String[] columnNames, Object[][] columns, int fieldLength) {
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.setLayout(new BorderLayout(0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        CheckBoxTableModel tableModel = new CheckBoxTableModel(columnNames, columns, fieldLength);
        table.setModel(tableModel);
        if (Arrays.asList(columnNames).contains("条件")) {
            table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(QueryTableUtils.comboBox));
        }
        table.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(table));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                Object obj = table.getValueAt(row, column);
                if (table.isCellSelected(row, column)) {
                    System.out.println(obj);
                }
            }
        });
    }

    private List<String> getSelectedTables() {
        List<String> listTables = new ArrayList<>();
        int count = tableBox.getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                listTables.add(tableBox.getItemAt(i).toString());
            }
        }
        return listTables;
    }

    private List<Integer> getSelectedTableIndexs(List<String> tables, List<String> selectedTables) {
        List<Integer> selectedIndexs = new ArrayList<>();
        if (tables != null && selectedTables != null) {
            int index = 0;
            for (String table : tables) {
                if (selectedTables.contains(table)) {
                    selectedIndexs.add(index);
                }
                index++;
            }
        }
        return selectedIndexs;
    }

    public void clearTab() {
        addTab.removeAll();
        editTab.removeAll();
        queryTab.removeAll();
        voTab.removeAll();
        exportTab.removeAll();
        domainField.setText("");
        primaryField.setText("");
        showExportLabel.setSelected(false);
        generatorKey.setSelected(false);
        tablePanel.setSelectedIndex(0);
        tablePanel.repaint();
    }

    public void clearSqlTab() {
        sqlVoTab.removeAll();
        sqlExportTab.removeAll();
        sqlQueryTab.removeAll();
        sqlDomainField.setText("");
        tablePanel.setSelectedIndex(0);
        tablePanel.repaint();
    }

    private void openTableFrame() {
        try {
            initContext();
        } catch (ZhqcException e1) {
            WarnDialog.showWarnDialog(e1.getContent(), null);
            return;
        }
        String dbName = databaseField.getText();
        List<String> tables = TableUtils.getTablesByDb(context, dbName);
        List<String> selectedTables = getSelectedTables();
        List<Integer> selectedIndexs = getSelectedTableIndexs(tables, selectedTables);
        tableFrame.setListData(tables);
        tableFrame.setSelectedList(selectedIndexs);
        tableFrame.setVisible(true);
        setParentEnabled(false);
    }

    private void initContext() {
        String dbName = databaseField.getText();
        String clientId = clientIdField.getText();
        String author = authorField.getText();
        String ip = dbIpField.getText();
        String port = portField.getText();
        String userName = userNameField.getText();
        String pwd = String.valueOf(pwdField.getPassword());
        String basePackage = packageField.getText();
        String basePath = outPathField.getText();
        if (StringUtils.isBlank(dbName) || StringUtils.isBlank(clientId) || StringUtils.isBlank(author)
                || StringUtils.isBlank(ip) || StringUtils.isBlank(port) || StringUtils.isBlank(userName)
                || StringUtils.isBlank(pwd) || StringUtils.isBlank(basePackage) || StringUtils.isBlank(basePath)) {
            throw new ZhqcException(300, "请填写上面的基本信息");
        }
        context = GeneratorUtils.buildContext(clientId, dbName, author, ip, port, userName, pwd, basePackage, basePath);
    }

    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        String author = authorField.getText();
        if (StringUtils.isNotBlank(author)) {
            config.put("author", author);
        }
        String basePackage = packageField.getText();
        if (StringUtils.isNotBlank(basePackage)) {
            config.put("basePackage", basePackage);
        }
        String basePath = outPathField.getText();
        if (StringUtils.isNotBlank(basePath)) {
            config.put("basePath", basePath);
        }
        String clientId = clientIdField.getText();
        if (StringUtils.isNotBlank(clientId)) {
            config.put("clientId", clientId);
        }
        String database = databaseField.getText();
        if (StringUtils.isNotBlank(database)) {
            config.put("database", database);
        }
        String host = dbIpField.getText();
        if (StringUtils.isNotBlank(host)) {
            config.put("host", host);
        }
        String port = portField.getText();
        if (StringUtils.isNotBlank(port)) {
            config.put("port", port);
        }
        String password = String.valueOf(pwdField.getPassword());
        if (StringUtils.isNotBlank(password)) {
            config.put("password", password);
        }
        String userName = userNameField.getText();
        if (StringUtils.isNotBlank(userName)) {
            config.put("username", userName);
        }
        return config;
    }

    private void setParentEnabled(boolean enabled) {
        this.setEnabled(enabled);
    }

    public JComboBox getTableBox() {
        return tableBox;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void initConfig(GeneratorConfig config) {
        packageField.setText(config.getBasePackage());
        outPathField.setText(config.getBasePath());
        clientIdField.setText(config.getClientId());
        authorField.setText(config.getAuthor());
        dbIpField.setText(config.getHost());
        portField.setText(config.getPort());
        databaseField.setText(config.getDatabase());
        userNameField.setText(config.getUsername());
        pwdField.setText(config.getPassword());
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        Font rootPanelFont = this.$$$getFont$$$(null, -1, 20, rootPanel.getFont());
        if (rootPanelFont != null) rootPanel.setFont(rootPanelFont);
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 8, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ipLabel = new JLabel();
        ipLabel.setText("数据库IP");
        ipLabel.setVerticalAlignment(1);
        ipLabel.setVerticalTextPosition(1);
        panel2.add(ipLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-1369272));
        label1.setText("*");
        panel2.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appLabel = new JLabel();
        appLabel.setEnabled(true);
        appLabel.setText("项目编码");
        appLabel.setVerticalAlignment(1);
        panel3.add(appLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setForeground(new Color(-1369272));
        label2.setText("*");
        panel3.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientIdField = new JTextField();
        clientIdField.setColumns(10);
        clientIdField.setText("");
        clientIdField.setToolTipText("输入项目编码");
        panel11.add(clientIdField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dbIpField = new JTextField();
        dbIpField.setColumns(10);
        dbIpField.setText("127.0.0.1");
        dbIpField.setToolTipText("");
        panel11.add(dbIpField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        authorLabel = new JLabel();
        authorLabel.setText("Author");
        panel4.add(authorLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setForeground(new Color(-1369272));
        label3.setText("*");
        panel4.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        portLabel = new JLabel();
        portLabel.setText("端口");
        panel5.add(portLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setForeground(new Color(-1369272));
        label4.setText("*");
        panel5.add(label4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portField = new JTextField();
        portField.setColumns(5);
        portField.setText("3306");
        panel11.add(portField, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel6, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setForeground(new Color(-1369272));
        label5.setText("*");
        panel6.add(label5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        databaseNameLabel = new JLabel();
        databaseNameLabel.setText("数据库名");
        panel6.add(databaseNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        databaseField = new JTextField();
        databaseField.setColumns(5);
        databaseField.setText("");
        databaseField.setToolTipText("输入要访问的数据库");
        panel11.add(databaseField, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        authorField = new JTextField();
        authorField.setColumns(5);
        authorField.setEditable(true);
        authorField.setText("");
        authorField.setToolTipText("输入创建者");
        panel11.add(authorField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel7, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        userNameLabel = new JLabel();
        userNameLabel.setText("用户名");
        panel7.add(userNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setForeground(new Color(-1369272));
        label6.setText("*");
        panel7.add(label6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel8, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pwdLabel = new JLabel();
        pwdLabel.setText("密码");
        panel8.add(pwdLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setForeground(new Color(-1369272));
        label7.setText("*");
        panel8.add(label7, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        userNameField = new JTextField();
        userNameField.setColumns(5);
        userNameField.setText("");
        userNameField.setToolTipText("输入数据库登录用户");
        panel11.add(userNameField, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pwdField = new JPasswordField();
        pwdField.setColumns(10);
        pwdField.setText("");
        pwdField.setToolTipText("输入数据库登录密码");
        panel11.add(pwdField, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        basePanel = new JPanel();
        basePanel.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(basePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        packageField = new JTextField();
        packageField.setColumns(20);
        packageField.setText("com.zhqc.business");
        packageField.setToolTipText("输入公共包名");
        basePanel.add(packageField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        outPathField = new JTextField();
        outPathField.setColumns(20);
        basePanel.add(outPathField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectPathBtn = new JButton();
        selectPathBtn.setText("选择文件夹");
        basePanel.add(selectPathBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        basePanel.add(panel9, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        outLabel = new JLabel();
        outLabel.setText("文件输出路径");
        panel9.add(outLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setForeground(new Color(-1369272));
        label8.setText("*");
        panel9.add(label8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        packagePanel = new JPanel();
        packagePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        basePanel.add(packagePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        packageLabel = new JLabel();
        Font packageLabelFont = this.$$$getFont$$$(null, -1, -1, packageLabel.getFont());
        if (packageLabelFont != null) packageLabel.setFont(packageLabelFont);
        packageLabel.setText("公共包名");
        packagePanel.add(packageLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pStart = new JLabel();
        pStart.setForeground(new Color(-1369272));
        pStart.setText("*");
        packagePanel.add(pStart, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        footPanel = new JPanel();
        footPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(footPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        submitBtn = new JButton();
        submitBtn.setText("点击生成代码");
        footPanel.add(submitBtn, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        footPanel.add(spacer1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        footPanel.add(spacer2, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        footPanel.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        footPanel.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        configPanel = new JTabbedPane();
        rootPanel.add(configPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        tableTab = new JPanel();
        tableTab.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        configPanel.addTab("按表配置", tableTab);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tableTab.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectTableBtn = new JButton();
        selectTableBtn.setText("点击勾选表");
        panel12.add(selectTableBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tableBox = new JComboBox();
        tableBox.setEditable(false);
        tableBox.setEnabled(true);
        panel12.add(tableBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel12.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        tablePanel = new JTabbedPane();
        tableTab.add(tablePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        baseTab = new JPanel();
        baseTab.setLayout(new GridLayoutManager(3, 8, new Insets(0, 0, 0, 0), -1, -1));
        tablePanel.addTab("基本配置", baseTab);
        domainLabel = new JLabel();
        domainLabel.setText("实体名称");
        baseTab.add(domainLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        domainField = new JTextField();
        domainField.setColumns(20);
        baseTab.add(domainField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer6 = new Spacer();
        baseTab.add(spacer6, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        baseTab.add(spacer7, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        baseTab.add(spacer8, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        primaryLabel = new JLabel();
        primaryLabel.setText("主键");
        baseTab.add(primaryLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        primaryField = new JTextField();
        primaryField.setColumns(20);
        baseTab.add(primaryField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        generatorKey = new JCheckBox();
        generatorKey.setText("useGeneratedKeys");
        baseTab.add(generatorKey, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        baseTab.add(spacer9, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        baseTab.add(spacer10, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        baseTab.add(spacer11, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        showExportLabel = new JCheckBox();
        showExportLabel.setText("增加导出配置");
        baseTab.add(showExportLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        voTab = new JPanel();
        voTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tablePanel.addTab("Model配置", voTab);
        queryTab = new JPanel();
        queryTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tablePanel.addTab("查询配置", queryTab);
        addTab = new JPanel();
        addTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tablePanel.addTab("新增配置", addTab);
        editTab = new JPanel();
        editTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tablePanel.addTab("修改配置", editTab);
        exportTab = new JPanel();
        exportTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tablePanel.addTab("导出配置", exportTab);
        sqlTab = new JPanel();
        sqlTab.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        configPanel.addTab("按SQL配置", sqlTab);
        sqlConfigPanel = new JPanel();
        sqlConfigPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        sqlTab.add(sqlConfigPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        sqlConfigPanel.add(panel13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sqlLabel = new JLabel();
        sqlLabel.setText("输入sql");
        panel13.add(sqlLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sqlScroll = new JScrollPane();
        sqlScroll.setHorizontalScrollBarPolicy(31);
        sqlConfigPanel.add(sqlScroll, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sqlArea = new JTextArea();
        sqlArea.setColumns(60);
        sqlArea.setLineWrap(true);
        sqlArea.setRows(10);
        sqlArea.setWrapStyleWord(true);
        sqlScroll.setViewportView(sqlArea);
        parseBtn = new JButton();
        parseBtn.setText("解析SQL");
        sqlConfigPanel.add(parseBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sqlConfigTab = new JTabbedPane();
        sqlTab.add(sqlConfigTab, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        sqlBaseTab = new JPanel();
        sqlBaseTab.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        sqlConfigTab.addTab("基本配置", sqlBaseTab);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        sqlBaseTab.add(panel14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sqlDomainLabel = new JLabel();
        sqlDomainLabel.setText("实体名称");
        panel14.add(sqlDomainLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sqlDomainField = new JTextField();
        panel14.add(sqlDomainField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer12 = new Spacer();
        sqlBaseTab.add(spacer12, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        sqlVoTab = new JPanel();
        sqlVoTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        sqlConfigTab.addTab("Model配置", sqlVoTab);
        sqlQueryTab = new JPanel();
        sqlQueryTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        sqlConfigTab.addTab("查询配置", sqlQueryTab);
        sqlExportTab = new JPanel();
        sqlExportTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        sqlConfigTab.addTab("导出配置", sqlExportTab);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
