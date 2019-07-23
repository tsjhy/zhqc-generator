package com.zhqc.business.generator.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class CheckFieldUtils {
    public static boolean checkFieldContainsField(String str,String field){
        if(StringUtils.isNotBlank(str)){
            String[] temp = str.split("\\,");
            List list = Arrays.asList(temp);
            return list.contains(field);
        }
        return false;
    }
}
