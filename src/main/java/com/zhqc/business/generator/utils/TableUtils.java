package com.zhqc.business.generator.utils;

import com.zhqc.framerwork.common.exception.ZhqcException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.generator.config.Context;

import java.sql.*;
import java.util.*;

/**
 * 获取表相关数据工具类
 */
public class TableUtils {
    private static Logger logger = Logger.getLogger(PropertiesUtils.class);
    private static String sqlTable = "";
    public static List<String> getTablesByDb(Context context, String dbName){
        Connection conn = null;
        List<String> tableNames = new ArrayList<>();
        try {
            conn = context.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null,dbName,"%",new String []{"TABLE", "VIEW"});
            while(rs.next()){
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e1) {
            logger.error("getTablesByDb case some error",e1);
            throw new ZhqcException(300,"获取表信息失败");
        }finally {
            context.closeConnection(conn);
        }
        return tableNames;
    }
    public static List<Map<String,Object>> getTableColumnsByTableName(Context context,String dbName,String table){
        Connection conn = null;
        List<Map<String,Object>> columnMapList = new ArrayList<>();
        try{
            conn = context.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null,dbName,"%",new String []{"TABLE", "VIEW"});
            while(rs.next()){
                String tableName = rs.getString("TABLE_NAME");
                if(tableName.equals(table)){
                    ResultSet pk = metaData.getPrimaryKeys(null, dbName, tableName);
                    StringBuilder pkBuild = new StringBuilder();
                    while( pk.next() ) {
                        pkBuild.append(pk.getObject(4)).append(",");
                    }
                    if(pkBuild.length()>0){
                        DataUtils.setPkField(tableName,pkBuild.toString().substring(0,pkBuild.toString().length()-1));
                    }
                    ResultSet rsc = metaData.getColumns(null,dbName,tableName.toUpperCase(), "%");
                    Map<String,Object> columnMap;
                    while(rsc.next()){
                        //System.out.println("字段名："+rs.getString("COLUMN_NAME")+"--字段注释："+rs.getString("REMARKS")+"--字段数据类型："+rs.getString("TYPE_NAME"));
                        columnMap = new LinkedHashMap<>(3);
                        String colName = rsc.getString("COLUMN_NAME");
                        columnMap.put("column",colName);
                        String dbType = rsc.getString("TYPE_NAME");
                        columnMap.put("dbType",changeDbType(dbType));
                        String remarks = rsc.getString("REMARKS");
                        if(StringUtils.isBlank(remarks)){
                            remarks = colName;
                        }
                        columnMap.put("remark",remarks);
                        columnMapList.add(columnMap);
                    }
                }
            }
        }catch(Exception e){
            logger.error("getTableColumnsByTableName case some error",e);
            throw new ZhqcException(300,"获取表字段信息失败");
        }finally {
            context.closeConnection(conn);
        }
        return columnMapList;
    }
    public static List<Map<String, Object>> buildColumns(List<Map<String, Object>> columns,Integer length) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map2;
        for(Map<String,Object> map : columns){
            map2 = new LinkedHashMap<>();
            map2.putAll(map);
            for(int i = 0;i<length;i++){
                map2.put("selected"+i,false);
            }
            result.add(map2);
        }
        return result;
    }
    public static Object[][] listToArray(List<Map<String,Object>> list,Integer keyLength) {
        if (CollectionUtils.isEmpty(list)) {
            return new Object[0][];
        }
        int size = list.size();
        Object[][] array = new Object[size][keyLength];
        for (int i = 0; i < size; i++) {//循环遍历所有行
            array[i] = list.get(i).values().toArray();//每行的列数
        }
        return array;
    }
    public static String changeDbType(String type) {
        String dbType = type.toUpperCase();
        switch(dbType){
            case "BLOB":
                return "Byte";
            case "VARCHAR":
            case "VARCHAR2":
            case "TEXT":
            case "CHAR":
                return "String";
            case "DOUBLE":
                return "Double";
            case "DECIMAL":
                return "BigDecimal";
            case "FLOAT":
                return "Float";
            case "INTEGER":
                return "Long";
            case "INT":
            case "TINYINT":
            case "SMALLINT":
            case "MEDIUMIINT":
            case "BOOLEAN":
                return "Integer";
            case "BIGINT":
                return "BigInteger";
            case "BIT":
                return "Boolean";
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                return "Date";
            default:
                return type;
        }
    }

    public static List<Map<String,Object>> parseSql(Context context, String dbName,String sql){
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;
        List<Map<String,Object>> columnMapList = new ArrayList<>();
        try {
            conn = context.getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData(); //通过ResultSetMetaData获取字段
            if(rsmd != null){
                int count = rsmd.getColumnCount();
                Map<String,Object> columnMap;
                Map<String,List<Map<String,Object>>> tableColumnsMap = new HashMap<>();
                Map<String,Integer> tableUseMap = new HashMap<>();
                List<Map<String,Object>> tableColumns;
                for(int i=1;i<=count;i++){
                    if(tableColumnsMap.containsKey(rsmd.getTableName(i))){
                        tableColumns = tableColumnsMap.get(rsmd.getTableName(i));
                        tableUseMap.put(rsmd.getTableName(i),tableUseMap.get(rsmd.getTableName(i))+1);
                    }else{
                        tableColumns = getTableColumnsByTableName(context,dbName,rsmd.getTableName(i));
                        tableColumnsMap.put(rsmd.getTableName(i),tableColumns);
                        tableUseMap.put(rsmd.getTableName(i),1);
                    }
                    columnMap = new HashMap<>();
                    columnMap.put("column",rsmd.getColumnName(i));
                    columnMap.put("dbType",changeDbType(rsmd.getColumnTypeName(i)));
                    columnMap.put("remark",getColumnRemark(rsmd.getColumnName(i),tableColumns));
                    columnMapList.add(columnMap);
                }
                int tempCount = 0;
                for(Map.Entry<String,Integer> entry : tableUseMap.entrySet()){
                    if(tempCount<entry.getValue()){
                        tempCount = entry.getValue();
                        sqlTable = entry.getKey();
                    }
                }
            }
            rs.close();
            statement.close();
        } catch (Exception e1) {
            logger.error("parseSql case some error",e1);
            throw new ZhqcException(300,"解析SQL失败");
        }finally {
            context.closeConnection(conn);
            try {
                if(rs != null){
                    rs.close();
                }
                if(statement != null){
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return columnMapList;
    }

    private static String getColumnRemark(String columnName, List<Map<String, Object>> tableColumns) {
        for(Map<String,Object> map : tableColumns){
            if(map.get("column").equals(columnName)){
                return (String) map.get("remark");
            }
        }
        return columnName;
    }

    public static String getSqlTable() {
        return sqlTable+"_sql";
    }
}
