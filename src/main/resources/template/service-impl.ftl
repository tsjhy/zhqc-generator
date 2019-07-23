package ${basePackage}.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
<#if generatorType == 'table'>
import ${basePackage}.model.ao.${modelNameUpperCamel}UpdateReq;
import ${basePackage}.model.ao.${modelNameUpperCamel}Req;
</#if>
import ${basePackage}.model.bo.${modelNameUpperCamel}BO;
import ${basePackage}.model.po.${modelNameUpperCamel}PO;
import ${basePackage}.model.po.${modelNameUpperCamel}POExample;
import ${basePackage}.model.query.${modelNameUpperCamel}Query;
import ${basePackage}.service.${modelNameUpperCamel}Service;
import ${basePackage}.dao.${modelNameUpperCamel}Dao;
import com.zhqc.framerwork.common.service.BaseService;
import com.zhqc.framerwork.common.util.AccountUtil;
import com.zhqc.framerwork.common.exception.ZhqcException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zhqc.framerwork.common.Enum.ResponseEnum;
import org.apache.commons.lang3.StringUtils;
import com.zhqc.framerwork.common.util.DateFormatUtil;
import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ${modelNameUpperCamel}ServiceImpl extends BaseService implements ${modelNameUpperCamel}Service {
    @Resource
    private ${modelNameUpperCamel}Dao ${modelNameLowerCamel}Dao;
    <#if generatorType == 'sql'>
    @Transactional(readOnly = true)
    @Override
    public PageInfo pageInfo(${modelNameUpperCamel}Query query) {
        PageHelper.startPage(query.getPage(),query.getLimit());
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByQuery(query);
        PageInfo page = new PageInfo(plist);
        page.setList(poJoCopyUtils.convert(page.getList(),${modelNameUpperCamel}BO.class));
        return page;
    }
    </#if>
    <#if generatorType == 'table'>
    @Transactional(readOnly = true)
    @Override
    public PageInfo pageInfo(${modelNameUpperCamel}Query query) {
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
        PageHelper.startPage(query.getPage(),query.getLimit());
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByExample(example);
        PageInfo page = new PageInfo(plist);
        page.setList(poJoCopyUtils.convert(page.getList(),${modelNameUpperCamel}BO.class));
        return page;
    }

    @Override
    public void add(${modelNameUpperCamel}Req req) {
        <#list primaryKeysUpper as query>
        if(null != get${modelNameUpperCamel}POBy${query.key}(req.get${query.key}())){
            throw new ZhqcException(ResponseEnum.DATA_EXISTS,<#if query.type != 'String'>String.valueOf(req.get${query.key}())<#else>req.get${query.key}()</#if>);
        }
        </#list>
        ${modelNameUpperCamel}PO po = poJoCopyUtils.convert(req,${modelNameUpperCamel}PO.class);
        po.setCreateTime(new Date());
        po.setCreator(AccountUtil.getCurrentUserNo());
        ${modelNameLowerCamel}Dao.insert(po);
    }

    @Override
    public void update(${modelNameUpperCamel}UpdateReq req) {
        ${modelNameUpperCamel}PO po = get${modelNameUpperCamel}POBy<#list primaryIdsUpper as key>${key}<#sep>And</#list>(<#list primaryIdsUpper as key>req.get${key}()<#sep>,</#list>);
        if(po == null){
            throw  new ZhqcException(ResponseEnum.DATA_NOT_EXISTS,<#list primaryIdsUpper as key>String.valueOf(req.get${key}())<#sep>,</#list>);
        }
        <#list primaryKeysUpper as query>
        existsByIdAnd${query.key}(req.get${query.key}(),<#list primaryIdsUpper as key>req.get${key}()<#sep>,</#list>);
        </#list>
        po = poJoCopyUtils.convert(req,${modelNameUpperCamel}PO.class);
        po.setUpdater(AccountUtil.getCurrentUserNo());
        po.setUpdateTime(new Date());
        ${modelNameLowerCamel}Dao.updateByPrimaryKeySelective(po);
    }

    @Override
    public void delete(Integer id) {
        ${modelNameLowerCamel}Dao.deleteByPrimaryKey(id);
    }
    /**
    * 根据主键查询${remark}
    <#list primaryIds as key>
    * @param ${key} 主键${key}
    </#list>
    * @return ${modelNameUpperCamel}PO  ${remark}
    */
    private ${modelNameUpperCamel}PO get${modelNameUpperCamel}POBy<#list primaryIdsUpper as key>${key}<#sep>And</#list>(<#list primaryIds as key>Integer ${key}<#sep>,</#list>){
        ${modelNameUpperCamel}POExample example = new ${modelNameUpperCamel}POExample();
        ${modelNameUpperCamel}POExample.Criteria criteria = example.createCriteria();
        <#list primaryIdsUpper as key>
        criteria.and${key}EqualTo(${primaryIds[key?index]});
        </#list>
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByExample(example);
        if(plist != null && plist.size()>0){
            return plist.get(0);
        }
        return null;
    }
    <#--<#if (primaryKeys?size &gt; 0)>-->
    <#--/**-->
    <#--* 查询${remark}-->
    <#--<#list primaryKeys as key>-->
    <#--* @param ${key} ${key}-->
    <#--</#list>-->
    <#--* @return ${modelNameUpperCamel}PO  ${remark}-->
    <#--*/-->
    <#--private ${modelNameUpperCamel}PO get${modelNameUpperCamel}POBy<#list primaryKeysUpper as key>${key}<#sep>And</#list>(<#list primaryKeys as key>${key.type} ${key}<#sep>,</#list>){-->
        <#--${modelNameUpperCamel}POExample example = new ${modelNameUpperCamel}POExample();-->
        <#--${modelNameUpperCamel}POExample.Criteria criteria = example.createCriteria();-->
        <#--<#list primaryKeysUpper as key>-->
        <#--criteria.and${key}EqualTo(${primaryKeys[key?index]});-->
        <#--</#list>-->
        <#--List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByExample(example);-->
        <#--if(plist != null && plist.size()>0){-->
            <#--return plist.get(0);-->
        <#--}-->
        <#--return null;-->
    <#--}-->
    <#--</#if>-->

    <#list primaryKeysUpper as query>
    /**
    * 根据${query.key}查询${remark}
    * @param ${primaryKeys[query?index]} ${primaryKeys[query?index]}
    * @return ${modelNameUpperCamel}PO  ${remark}
    */
    private ${modelNameUpperCamel}PO get${modelNameUpperCamel}POBy${query.key}(${query.type} ${primaryKeys[query?index]}){
        ${modelNameUpperCamel}POExample example = new ${modelNameUpperCamel}POExample();
        ${modelNameUpperCamel}POExample.Criteria criteria = example.createCriteria();
        criteria.and${query.key}EqualTo(${primaryKeys[query?index]});
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByExample(example);
        if(plist != null && plist.size()>0){
            return plist.get(0);
        }
        return null;
    }
    /**
     * 修改时校验是否存在${primaryKeys[query?index]}相同的数据
     * @param ${primaryKeys[query?index]} ${primaryKeys[query?index]}
     <#list primaryIds as key>
     * @param ${key} 修改数据主键${key}
     </#list>
     */
    private void existsByIdAnd${query.key}(${query.type} ${primaryKeys[query?index]},<#list primaryIds as key>Integer ${key}<#sep>,</#list>){
        ${modelNameUpperCamel}POExample example = new ${modelNameUpperCamel}POExample();
        ${modelNameUpperCamel}POExample.Criteria criteria = example.createCriteria();
        criteria.and${query.key}EqualTo(${primaryKeys[query?index]});
        <#list primaryIdsUpper as key>
        criteria.and${key}NotEqualTo(${primaryIds[key?index]});
        </#list>
        List<${modelNameUpperCamel}PO> plist = ${modelNameLowerCamel}Dao.selectByExample(example);
        if(plist != null && plist.size()>0){
            throw  new ZhqcException(ResponseEnum.DATA_EXISTS,<#if query.type != 'String'>String.valueOf(${primaryKeys[query?index]})<#else>${primaryKeys[query?index]}</#if>);
        }
    }
    </#list>
    </#if>
}
