package com.zhqc.business.generator.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddTableUtils {

    private static String[] columnNames = {"列名","类型","备注","新增字段","屏蔽描述","屏蔽校验","不能重复"};
    public static final int commonLength = 3;
    public static String[] getColumnNames(){
        return columnNames;
    }
}
