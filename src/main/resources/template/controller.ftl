package ${basePackage}.controller;

import com.github.pagehelper.PageInfo;
<#if generatorType == 'table'>
import ${basePackage}.model.ao.${modelNameUpperCamel}UpdateReq;
import ${basePackage}.model.ao.${modelNameUpperCamel}Req;
</#if>
import ${basePackage}.model.query.${modelNameUpperCamel}Query;
import ${basePackage}.model.vo.${modelNameUpperCamel}VO;
import ${basePackage}.service.${modelNameUpperCamel}Service;
import com.zhqc.framerwork.common.controller.BaseController;
import com.zhqc.framerwork.common.interfaces.valid.Add;
import com.zhqc.framerwork.common.interfaces.valid.Edit;
import com.zhqc.framerwork.common.model.vo.PageResponseVo;
import com.zhqc.framerwork.common.model.vo.ResponseVo;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
/**
 * ${remark}Controller
 * @author ${author} ${date}
 */
@Controller
@RequestMapping("/${baseRequestMapping}/")
@Api(value="${remark}")
public class ${modelNameUpperCamel}Controller extends BaseController {

    @Resource
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @ApiOperation(value="查询${remark}")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "String", paramType = "header")})
    @PreAuthorize("hasRole('${clientId}_QUERY_${modelCode}')")
    @RequestMapping(value = "/pageInfo",method = RequestMethod.POST)
    @ResponseBody
    public PageResponseVo<List<${modelNameUpperCamel}VO>> pageInfo(@RequestBody ${modelNameUpperCamel}Query query) {
        PageInfo page = ${modelNameLowerCamel}Service.pageInfo(query);
        PageResponseVo<List<${modelNameUpperCamel}VO>> vo = PageResponseVo.buildSuccess();
        if(page != null){
            vo.setObj(page.getList());
            vo.setTotal(page.getTotal());
        }
        return vo;
    }
    <#if generatorType == 'table'>
    @ApiOperation(value="新增${remark}")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "String", paramType = "header")})
    @PreAuthorize("hasRole('${clientId}_ADD_${modelCode}')")
    @RequestMapping(value = "/add" , method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo add(@RequestBody @Validated({Add.class}) ${modelNameUpperCamel}Req req) {
        ${modelNameLowerCamel}Service.add(req);
        return ResponseVo.SYS_SUCCESS;
    }
    @ApiOperation(value="修改${remark}")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "String", paramType = "header")})
    @PreAuthorize("hasRole('${clientId}_UPDATE_${modelCode}')")
    @RequestMapping(value = "/update" , method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo update(@RequestBody @Validated({Edit.class}) ${modelNameUpperCamel}UpdateReq req) {
        ${modelNameLowerCamel}Service.update(req);
        return ResponseVo.SYS_SUCCESS;
    }

    @ApiOperation(value="删除${remark}")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "String", paramType = "header")})
    @PreAuthorize("hasRole('${clientId}_DEL_${modelCode}')")
    @ResponseBody
    @RequestMapping(value = "/delete/{id}" , method = RequestMethod.POST)
    public ResponseVo delete(@ApiParam(value="主键ID",required = true)@PathVariable("id") Integer id) {
        ${modelNameLowerCamel}Service.delete(id);
        return ResponseVo.SYS_SUCCESS;
    }
    </#if>
}

