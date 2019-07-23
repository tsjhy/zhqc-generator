package com.zhqc.business.generator.utils;

import java.util.List;
import java.util.Map;

public class EditTableUtils {

    private static String[] columnNames = {"列名","类型","备注","修改字段","屏蔽描述","屏蔽基本校验"};
    public static final int commonLength = 3;
    public static String[] getColumnNames(){
        return columnNames;
    }
}
