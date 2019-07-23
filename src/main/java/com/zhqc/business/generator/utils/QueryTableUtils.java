package com.zhqc.business.generator.utils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryTableUtils {

    private static String[] columnNames = {"列名","类型","备注","条件","显示字段","WHERE条件","屏蔽描述","屏蔽校验"};
    public static final int commonLength = 4;
    public static String[] getColumnNames(){
        return columnNames;
    }

    public static JComboBox comboBox = new JComboBox();
    static {
        comboBox.addItem("=");
        comboBox.addItem("like");
        comboBox.addItem(">");
        comboBox.addItem(">=");
        comboBox.addItem("<");
        comboBox.addItem("<=");
        comboBox.addItem(">=&&<=");
        comboBox.addItem("!=");
    }
    public static String convertCondition(String condition){
        switch(condition){
            case "=":
                return "EqualTo";
            case "like":
                return "Like";
            case ">":
                return "GreaterThan";
            case ">=":
                return "GreaterThanOrEqualTo";
            case "<":
                return "LessThan";
            case "<=":
                return "LessThanOrEqualTo";
            case ">=&&<=":
                return "Between";
            case "!=":
                return "NotEqualTo";
            default:
                return condition;
        }
    }
    public static  List<Map<String, Object>> buildQueryColumns(List<Map<String, Object>> columns) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map2;
        for(Map<String,Object> map : columns){
            map2 = new LinkedHashMap<>();
            map2.putAll(map);
            map2.put("condition","=");
            for(int i = 0;i<columnNames.length-commonLength;i++){
                map2.put("selected"+i,false);
            }
            result.add(map2);
        }
        return result;
    }
}
