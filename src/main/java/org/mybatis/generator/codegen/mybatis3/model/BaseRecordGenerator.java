/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.model;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;

import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 *
 * @author Jeff Butler
 *
 */
public class BaseRecordGenerator extends AbstractJavaGenerator {

    public BaseRecordGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.8", table.toString())); //$NON-NLS-1$
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);
        List<IntrospectedColumn> introspectedColumns;
        if("sql".equals(introspectedTable.getTableConfiguration().getProperties().getProperty("generatorType"))){
            introspectedColumns = getColumnsInThisClassBySql();
        }else{
            introspectedColumns = getColumnsInThisClass();
        }

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass, introspectedTable.getNonBLOBColumns());

            if (includeBLOBColumns()) {
                addParameterizedConstructor(topLevelClass, introspectedTable.getAllColumns());
            }

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }

        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                    .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass()
                && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass()
                && introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass, List<IntrospectedColumn> constructorColumns) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        StringBuilder sb = new StringBuilder();
        List<String> superColumns = new LinkedList<String>();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super("); //$NON-NLS-1$
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
                superColumns.add(introspectedColumn.getActualColumnName());
            }
            sb.append(");"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        }

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            if (!superColumns.contains(introspectedColumn.getActualColumnName())) {
                sb.setLength(0);
                sb.append("this."); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" = "); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(';');
                method.addBodyLine(sb.toString());
            }
        }

        topLevelClass.addMethod(method);
    }

    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                        .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }

        return introspectedColumns;
    }

    private List<IntrospectedColumn> getColumnsInThisClassBySql() {
        Properties properties = introspectedTable.getTableConfiguration().getProperties();
        String requiredFields = properties.getProperty("po_requiredField");
        String fieldRemarks = properties.getProperty("po_fieldRemark");
        String fieldTypes = properties.getProperty("po_fieldType");

        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        List<IntrospectedColumn> result = new ArrayList<>();
        if(StringUtils.isNotBlank(requiredFields)){
            String[] temp = requiredFields.split("\\,");
            String[] remarkTemp = fieldRemarks.split("\\,");
            String[] fieldTypesTemp = fieldTypes.split("\\,");
            List<String> list = Arrays.asList(temp);
            List<String> remarkList = Arrays.asList(remarkTemp);
            List<String> typeList = Arrays.asList(fieldTypesTemp);
            boolean exists;
            int index = 0;
            IntrospectedColumn tempC;
            for(String column : list){
                exists = false;
                for(IntrospectedColumn introspectedColumn : columns){
                    if(column.equals(introspectedColumn.getActualColumnName())){
                        result.add(introspectedColumn);
                        exists = true;
                        break;
                    }
                }
                if(!exists){
                    tempC = buildIntrospectedColumn(column,remarkList.get(index),columns.get(0).getIntrospectedTable(),getType(typeList.get(index)),getJdbcType(typeList.get(index)));
                    introspectedTable.addColumn(tempC);
                    result.add(tempC);
                }
                index++;
            }
        }
        return result;
    }
    private FullyQualifiedJavaType getType(String type){
        switch (type){
            case "String":
                return PrimitiveTypeWrapper.getStringInstance();
            case "Integer":
                return PrimitiveTypeWrapper.getIntegerInstance();
            case "Boolean":
                return PrimitiveTypeWrapper.getBooleanInstance();
            case "Date":
                return PrimitiveTypeWrapper.getDateInstance();
            case "Long":
                return PrimitiveTypeWrapper.getLongInstance();
            case "Float":
                return PrimitiveTypeWrapper.getFloatInstance();
            case "Double":
                return PrimitiveTypeWrapper.getDoubleInstance();
            case "Byte":
                return PrimitiveTypeWrapper.getByteInstance();
                default:return PrimitiveTypeWrapper.getStringInstance();
        }
    }
    private String getJdbcType(String type){
        switch (type){
            case "String":
                return "VARCHAR";
            case "Integer":
                return "INTEGER";
            case "Boolean":
                return "BOOLEAN";
            case "Date":
                return "DATE";
            case "Long":
                return "BIGINT";
            case "Float":
                return "FLOAT";
            case "Double":
                return "DOUBLE";
            case "Byte":
                return "VARBINARY";
            default:return "VARCHAR";
        }
    }
    private IntrospectedColumn buildIntrospectedColumn(String columnName,String remark,IntrospectedTable table,FullyQualifiedJavaType type,String jdbcType){
        IntrospectedColumn column = new IntrospectedColumn();
        column.setActualColumnName(columnName);
        column.setRemarks(remark);
        column.setLength(100);
        column.setJavaProperty(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,columnName));
        column.setFullyQualifiedJavaType(type);
        column.setJdbcTypeName(jdbcType);
        column.setIntrospectedTable(table);
        return column;
    }
}