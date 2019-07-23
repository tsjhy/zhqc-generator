package com.zhqc.business.generator.utils;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class MyCommentGenerator extends DefaultCommentGenerator {
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDateContent(String dateContent) {
        this.dateContent = dateContent;
    }

    private String author;
    private String date;
    private String dateContent;
    private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";

    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        author = properties.getProperty("author");
        author = author == null ? "system" : author;
        date = properties.getProperty("date");
        date = date == null ? DEFAULT_DATE_FORMAT : date;
        SimpleDateFormat sf = new SimpleDateFormat(date);
        dateContent = sf.format(new Date());
    }
    //类上添加注释
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        String remark = introspectedTable.getRemarks();
        if (StringUtils.isNotBlank(remark)) {
            if(isQuery(topLevelClass.getType().getPackageName())){
                topLevelClass.addJavaDocLine(" * 接收前端查询" + remark+"请求对象");
            }else if(isVo(topLevelClass.getType().getPackageName())){
                topLevelClass.addJavaDocLine(" * 前端展示" + remark+"对象");
            }else if(isAo(topLevelClass.getType().getPackageName())){
                if(topLevelClass.getType().toString().contains("UpdateReq")){
                    topLevelClass.addJavaDocLine(" * 接收前端修改" + remark+"请求对象");
                }else{
                    topLevelClass.addJavaDocLine(" * 接收前端新增" + remark+"请求对象");
                }
            }else{
                topLevelClass.addJavaDocLine(" * " + remark);
            }
        }
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * " + "@author " + getAuthor()+" "+getDateContent());
        topLevelClass.addJavaDocLine(" */");
        if(isApiModel(topLevelClass.getType().getPackageName())){
            topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
            topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
            topLevelClass.addAnnotation("@ApiModel");
            if(isAo(topLevelClass.getType().getPackageName()) || isQuery(topLevelClass.getType().getPackageName())){
                topLevelClass.addImportedType("javax.validation.constraints.*");
                topLevelClass.addImportedType("org.hibernate.validator.constraints.Length");
            }
        }
    }
    public String getAuthor() {
        return author;
    }

    public String getDateContent() {
        return dateContent;
    }
    //属性上添加注释
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String remark = introspectedColumn.getRemarks();
        if (StringUtils.isNotBlank(remark)) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + remark);
            field.addJavaDocLine(" */");
        }
    }
    public void addFieldValidateComment(Field field,IntrospectedTable introspectedTable,  IntrospectedColumn introspectedColumn,String type){
        String remark = introspectedColumn.getRemarks();
        if (StringUtils.isNotBlank(remark)) {
            StringBuilder builder = new StringBuilder();
            if(!introspectedColumn.isNullable()){
                if(!type.equals("add") && introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn)){
                    if(field.getType().toString().equals("java.lang.String")){
                        builder.append("@NotBlank(message = \"").append(remark).append("不能为空\")").append("\n\t");

                    }else if(field.getType().toString().equals("java.lang.Integer")){
                        builder.append("@NotNull(message = \"").append(remark).append("不能为空\")");
                    }
                }
            }
            if(field.getType().toString().equals("java.lang.String")){
                builder.append("@Length(max = ").append(introspectedColumn.getLength()).append(",message=\"")
                .append(remark).append("长度不能超过").append(introspectedColumn.getLength()).append("\")");
            }
            if(builder.length() >0){
                field.addAnnotation(builder.toString());
            }
        }
    }
    public void addFieldApiModelComment(Field field,IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn,String type) {
        String remark = introspectedColumn.getRemarks();
        if (StringUtils.isNotBlank(remark)) {
            StringBuilder builder = new StringBuilder();
            if(type.equals("add") && introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn)){
                //新增方法主键隐藏
                builder.append("@ApiModelProperty(hidden=true)");
            }else{
                builder.append("@ApiModelProperty(value=\"")
                        .append(remark);
                if(!introspectedColumn.isNullable()){
                    builder.append(",必填");
                }
                if(!field.getType().toString().equals("java.util.Date")){
                    builder.append(",长度").append(introspectedColumn.getLength());
                }
                builder.append("\"");
                if(!introspectedColumn.isNullable()){
                    builder.append(",required=true");
                }
                builder.append(",dataType =\"").append(field.getType()).append("\")");
            }
            field.addAnnotation(builder.toString());

        }
    }

    //get方法添加注释
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
    }

    //setter方法添加注释
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }
    @Override
    public void addComment(XmlElement xmlElement) {

    }
    private boolean isApiModel(String packageName){
        return isAo(packageName) ||
                isQuery(packageName) ||
                isVo(packageName);
    }
    private boolean isQuery(String packageName){
        return packageName.contains("query");
    }
    private boolean isBo(String packageName){
        return packageName.contains("bo");
    }
    private boolean isVo(String packageName){
        return packageName.contains("vo");
    }
    private boolean isAo(String packageName){
        return packageName.contains("ao");
    }
}
