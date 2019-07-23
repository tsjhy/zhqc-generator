package com.zhqc.business.generator.utils;

import com.zhqc.business.generator.model.ao.TableConfigReq;

import java.util.*;

public class DataUtils {
    private static Map<String, TableConfigReq> dataMap = new HashMap<>();
    private static Map<String, TabConfig> tabConfigMap = new HashMap<>();

    public static Map<String, TableConfigReq> getDataMap() {
        return dataMap;
    }

    public static Map<String, TabConfig> getTabConfigMap() {
        return tabConfigMap;
    }

    public static void saveAddTabData(Object[][] addTabData, String tableName) {
        if (tabConfigMap.containsKey(tableName)) {
            Object[][] oldData = tabConfigMap.get(tableName).getAddTabConfig();
            if (oldData != null) {
                updateNewData(addTabData, oldData, AddTableUtils.commonLength - 1);
            }
            tabConfigMap.get(tableName).setAddTabConfig(addTabData);
        } else {
            DataUtils utils = new DataUtils();
            TabConfig tabConfig = utils.new TabConfig();
            tabConfig.setAddTabConfig(addTabData);
            tabConfigMap.put(tableName, tabConfig);
        }
    }

    public static void saveEditTabData(Object[][] editTabData, String tableName) {
        if (tabConfigMap.containsKey(tableName)) {
            Object[][] oldData = tabConfigMap.get(tableName).getEditTabConfig();
            if (oldData != null) {
                updateNewData(editTabData, oldData, EditTableUtils.commonLength - 1);
            }
            tabConfigMap.get(tableName).setEditTabConfig(editTabData);
        } else {
            DataUtils utils = new DataUtils();
            TabConfig tabConfig = utils.new TabConfig();
            tabConfig.setEditTabConfig(editTabData);
            tabConfigMap.put(tableName, tabConfig);
        }
    }

    public static void saveQueryTabData(Object[][] queryTabData, String tableName) {
        if (tabConfigMap.containsKey(tableName)) {
            Object[][] oldData = tabConfigMap.get(tableName).getQueryTabConfig();
            if (oldData != null) {
                updateNewData(queryTabData, oldData, QueryTableUtils.commonLength - 2);
            }
            tabConfigMap.get(tableName).setQueryTabConfig(queryTabData);
        } else {
            DataUtils utils = new DataUtils();
            TabConfig tabConfig = utils.new TabConfig();
            tabConfig.setQueryTabConfig(queryTabData);
            tabConfigMap.put(tableName, tabConfig);
        }
    }
    public static void saveExportTabData(Object[][] exportTabData, String tableName) {
        if (tabConfigMap.containsKey(tableName)) {
            Object[][] oldData = tabConfigMap.get(tableName).getExportTabConfig();
            if (oldData != null) {
                updateNewData(exportTabData, oldData, ExportTableUtils.commonLength - 2);
            }
            tabConfigMap.get(tableName).setExportTabConfig(exportTabData);
        } else {
            DataUtils utils = new DataUtils();
            TabConfig tabConfig = utils.new TabConfig();
            tabConfig.setExportTabConfig(exportTabData);
            tabConfigMap.put(tableName, tabConfig);
        }
    }
    public static void saveVoTabData(Object[][] voTabData, String tableName) {
        if (tabConfigMap.containsKey(tableName)) {
            Object[][] oldData = tabConfigMap.get(tableName).getVoTabConfig();
            if (oldData != null) {
                updateNewData(voTabData, oldData, VOTableUtils.commonLength - 1);
            }
            tabConfigMap.get(tableName).setVoTabConfig(voTabData);
        } else {
            DataUtils utils = new DataUtils();
            TabConfig tabConfig = utils.new TabConfig();
            tabConfig.setVoTabConfig(voTabData);
            tabConfigMap.put(tableName, tabConfig);
        }
    }

    private static void updateNewData(Object[][] newData, Object[][] oldData, int length) {
        for (int i = 0; i < newData.length; i++) {
            for (int n = 0; n < oldData.length; n++) {
                if (oldData[n][0].equals(newData[i][0])) {
                    for (int m = 0; m < oldData[n].length; m++) {
                        if (m > length) {
                            newData[i][m] = oldData[n][m];
                        }
                    }
                    break;
                }
            }
        }
    }
    public static String getDomainName(String tableName){
        if(dataMap.containsKey(tableName)){
            return dataMap.get(tableName).getDomainName();
        }
        return null;
    }
    public static void setDomainName(String tableName, String domainName) {
        TableConfigReq req;
        if(dataMap.containsKey(tableName)){
            req = dataMap.get(tableName);
        }else{
            req = new TableConfigReq();
        }
        req.setDomainName(domainName);
        dataMap.put(tableName,req);
    }

    public static TableConfigReq getTableConfigReq(String table) {
        if(dataMap.containsKey(table)){
            return dataMap.get(table);
        }else{
            TableConfigReq req = new TableConfigReq();
            dataMap.put(table,req);
            return req;
        }
    }
    public static String getPkField(String tableName){
        if(dataMap.containsKey(tableName)){
            return dataMap.get(tableName).getPrimaryField();
        }
        return "";
    }
    public static void setPkField(String tableName, String fieldName) {
        TableConfigReq req;
        if(dataMap.containsKey(tableName)){
            req = dataMap.get(tableName);
        }else{
            req = new TableConfigReq();
        }
        req.setPrimaryField(fieldName);
        dataMap.put(tableName,req);
    }

    public static boolean getUseGeneratedKey(String tableName) {
        TableConfigReq req;
        if(dataMap.containsKey(tableName)){
            req = dataMap.get(tableName);
        }else{
            req = new TableConfigReq();
        }
        return req.isUseGeneratedKeys();
    }

    public static List<TableConfigReq> getTabConfigList(List<String> tables) {
        List<TableConfigReq> list = new ArrayList<>();
        for(String table : tables){
            list.add(dataMap.get(table));
        }
        return list;
    }

    public static boolean getShowExport(String tableName) {
        TableConfigReq req;
        if(dataMap.containsKey(tableName)){
            req = dataMap.get(tableName);
        }else{
            req = new TableConfigReq();
        }
        return req.isShowExport();
    }

    class TabConfig {
        Object[][] addTabConfig;
        Object[][] editTabConfig;
        Object[][] queryTabConfig;
        Object[][] voTabConfig;
        Object[][] exportTabConfig;

        private Object[][] getAddTabConfig() {
            return addTabConfig;
        }

        private void setAddTabConfig(Object[][] addTabConfig) {
            this.addTabConfig = addTabConfig;
        }

        private Object[][] getEditTabConfig() {
            return editTabConfig;
        }

        private void setEditTabConfig(Object[][] editTabConfig) {
            this.editTabConfig = editTabConfig;
        }

        private Object[][] getQueryTabConfig() {
            return queryTabConfig;
        }

        private void setQueryTabConfig(Object[][] queryTabConfig) {
            this.queryTabConfig = queryTabConfig;
        }

        private Object[][] getVoTabConfig() {
            return voTabConfig;
        }

        private void setVoTabConfig(Object[][] voTabConfig) {
            this.voTabConfig = voTabConfig;
        }
        private Object[][] getExportTabConfig() {
            return exportTabConfig;

        }
        private void setExportTabConfig(Object[][] exportTabConfig) {
            this.exportTabConfig = exportTabConfig;
        }
    }
}
