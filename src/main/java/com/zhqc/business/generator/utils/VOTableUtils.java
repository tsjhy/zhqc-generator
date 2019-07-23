package com.zhqc.business.generator.utils;

import java.util.List;
import java.util.Map;

public class VOTableUtils {

    private static String[] columnNames = {"列名","类型","备注","VO属性","BO属性"};
    public static final int commonLength = 3;
    public static String[] getColumnNames(){
        return columnNames;
    }
}
