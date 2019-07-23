package ${basePackage}.service.impl;

import ${basePackage}.model.bo.${modelNameUpperCamel}BO;
import ${basePackage}.model.po.${modelNameUpperCamel}PO;
import ${basePackage}.model.po.${modelNameUpperCamel}POExample;
import ${basePackage}.model.query.${modelNameUpperCamel}Query;
import ${basePackage}.dao.${modelNameUpperCamel}Dao;
import com.zhqc.framerwork.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import com.zhqc.framerwork.common.util.DateFormatUtil;
import com.zhqc.framerwork.common.exception.ZhqcException;
import com.zhqc.framerwork.common.Enum.ResponseEnum;
import com.zhqc.framerwork.common.component.excel.common.*;
import javax.annotation.Resource;
import java.util.*;

@Service("${modelNameLowerCamel}ExportService")
@Transactional
public class ${modelNameUpperCamel}ExportServiceImpl extends BaseService implements ExportSingleSheetInterface<${modelNameUpperCamel}Query> {
    @Resource
    private ${modelNameUpperCamel}Dao ${modelNameLowerCamel}Dao;

    <#if generatorType == 'sql'>
    @Transactional(readOnly = true)
    @Override
    public List<${modelNameUpperCamel}BO> getExportData(${modelNameUpperCamel}Query query){
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByQuery(query);
        return poJoCopyUtils.convert(plist,${modelNameUpperCamel}BO.class);
    }
    </#if>
    <#if generatorType == 'table'>
    @Transactional(readOnly = true)
    @Override
    public List<${modelNameUpperCamel}BO> getExportData(${modelNameUpperCamel}Query query){
        ${modelNameUpperCamel}POExample example = new ${modelNameUpperCamel}POExample();
        ${modelNameUpperCamel}POExample.Criteria criteria = example.createCriteria();
        <#list queryKeysUpper as query>
        <#if query.type == 'Date'>
        <#if query.condition == 'Between'>
        Date startTime = null;
        Date endTime = null;
        try {
            if(StringUtils.isNotBlank(query.get${query.key}Start())){
                startTime = DateFormatUtil.parseTime(query.get${query.key}Start()+" 00:00:00");
            }
            if(StringUtils.isNotBlank(query.get${query.key}End())){
                endTime =  DateFormatUtil.parseTime(query.get${query.key}End()+" 23:59:59");
            }
        } catch (Exception e) {
            throw new ZhqcException(ResponseEnum.PARSE_DATE_ERROR);
        }
        criteria.and${query.key}${query.condition}(startTime,endTime);
        <#elseif query.condition == 'Like'>
        if(StringUtils.isNotBlank(query.get${query.key}())){
            criteria.and${query.key}${query.condition}("%"+query.get${query.key}()+"%");
        }
        <#else>
        if(null != query.get${query.key}()){
            criteria.and${query.key}${query.condition}(query.get${query.key}());
        }
        </#if>
        <#else>
        <#if query.condition == 'Between'>
        criteria.and${query.key}${query.condition}(query.get${query.key}Start(),query.get${query.key}End());
        <#else>
        <#if query.type == 'String'>
        if(StringUtils.isNotBlank(query.get${query.key}())){
            criteria.and${query.key}${query.condition}(query.get${query.key}());
        }
        <#else>
        if(null != query.get${query.key}()){
            criteria.and${query.key}${query.condition}(query.get${query.key}());
        }
        </#if>
        </#if>
        </#if>
        </#list>
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByExample(example);
        return poJoCopyUtils.convert(plist,${modelNameUpperCamel}BO.class);
    }
    </#if>
}
