package org.mybatis.generator.codegen.mybatis3.model;

import com.google.common.base.CaseFormat;
import com.zhqc.business.generator.utils.CheckFieldUtils;
import com.zhqc.business.generator.utils.MyCommentGenerator;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class QueryGenerator extends AbstractJavaGenerator {
    public QueryGenerator() {
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
                introspectedTable.getQueryRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        List<IntrospectedColumn> introspectedColumns =  getIntrospectedColumns();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);

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
            Properties properties = introspectedTable.getTableConfiguration().getProperties();
            String hiddenApiField = properties.getProperty("query_hiddenApiField");
            String hiddenValidateField = properties.getProperty("query_hiddenValidateField");
            boolean hiddenApi = StringUtils.isNotBlank(hiddenApiField) && introspectedColumn.getActualColumnName()!= null && CheckFieldUtils.checkFieldContainsField(hiddenApiField,introspectedColumn.getActualColumnName());
            boolean hiddenValid = StringUtils.isNotBlank(hiddenValidateField) && introspectedColumn.getActualColumnName()!= null && CheckFieldUtils.checkFieldContainsField(hiddenValidateField,introspectedColumn.getActualColumnName());
            MyCommentGenerator myCommentGenerator = ((MyCommentGenerator)context.getCommentGenerator());
            if(!hiddenApi){
                myCommentGenerator.addFieldApiModelComment(field,introspectedTable,introspectedColumn,"query");
            }
            if(!hiddenValid){
                myCommentGenerator.addFieldValidateComment(field,introspectedTable,introspectedColumn,"query");
            }
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

    private List<IntrospectedColumn> getIntrospectedColumns(){
        Properties properties = introspectedTable.getTableConfiguration().getProperties();
        String requiredFields = properties.getProperty("query_requiredField");
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        if(requiredFields != null){
            columns.removeIf(obj -> !CheckFieldUtils.checkFieldContainsField(requiredFields,obj.getActualColumnName()));
        }
        String queryKeys = properties.getProperty("queryKey");
        if(StringUtils.isNotBlank(queryKeys) && (queryKeys.contains("&Between&"))){
            String[] temp = queryKeys.split("\\,");
            for(String ss : temp){
                if(ss.contains("Between")){
                    String tempC = ss.split("\\&")[0];
                    String type = ss.split("\\&")[2];
                    IntrospectedColumn column = new IntrospectedColumn();
                    column.setJavaProperty(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,tempC)+"Start");
                    FullyQualifiedJavaType fullyQualifiedJavaType;
                    if(type.equals("Integer")){
                        fullyQualifiedJavaType = PrimitiveTypeWrapper.getIntegerInstance();
                    }else{
                        fullyQualifiedJavaType = PrimitiveTypeWrapper.getStringInstance();
                    }
                    column.setFullyQualifiedJavaType(fullyQualifiedJavaType);
                    column.setIntrospectedTable(columns.get(0).getIntrospectedTable());
                    columns.add(column);
                    column = new IntrospectedColumn();
                    column.setJavaProperty(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,tempC)+"End");
                    column.setFullyQualifiedJavaType(fullyQualifiedJavaType);
                    column.setIntrospectedTable(columns.get(0).getIntrospectedTable());
                    columns.add(column);
                }
            }
        }

        return columns;
    }
    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            Properties properties = context
                    .getJavaQueryGeneratorConfiguration().getProperties();
            String rootClass = properties.getProperty(PropertyRegistry.ANY_ROOT_CLASS);
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }
    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        List<IntrospectedColumn> constructorColumns = introspectedTable.getAllColumns() ;

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        StringBuilder sb = new StringBuilder();
        boolean comma = false;
        sb.append("super("); //$NON-NLS-1$
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getPrimaryKeyColumns()) {
            if (comma) {
                sb.append(", "); //$NON-NLS-1$
            } else {
                comma = true;
            }
            sb.append(introspectedColumn.getJavaProperty());
        }
        sb.append(");"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());

        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();;

        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.setLength(0);
            sb.append("this."); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }

}
