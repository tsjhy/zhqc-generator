package com.zhqc.business.generator.utils;

public class ExportTableUtils {

    private static String[] columnNames = {"列名","类型","备注","导出字段"};
    public static final int commonLength = 3;
    public static String[] getColumnNames(){
        return columnNames;
    }
}
