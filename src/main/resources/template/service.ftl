package ${basePackage}.service;

import com.github.pagehelper.PageInfo;
<#if generatorType == 'table'>
import ${basePackage}.model.ao.${modelNameUpperCamel}UpdateReq;
import ${basePackage}.model.ao.${modelNameUpperCamel}Req;
</#if>
import ${basePackage}.model.query.${modelNameUpperCamel}Query;
/**
 * ${remark}Service
 * @author ${author} ${date}
 */
public interface ${modelNameUpperCamel}Service {
    /**
     * 查询${remark}
     * @param query 接收查询${remark}条件对象
     */
    PageInfo pageInfo(${modelNameUpperCamel}Query query);
    <#if generatorType == 'table'>
    /**
     * 新增${remark}
     * @param req 新增${remark}对象
     */
    void add(${modelNameUpperCamel}Req req);
    /**
     * 修改${remark}
     * @param req 修改${remark}对象
     */
    void update(${modelNameUpperCamel}UpdateReq req);
    /**
     * 删除${remark}
     * @param id 主键ID
     */
    void delete(Integer id);
    </#if>
}

