package com.zhqc.business.generator.plugins;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.zhqc.business.generator.ZhqcGenerator;
import com.zhqc.business.generator.utils.ExcelUtil;
import com.zhqc.business.generator.utils.MyCommentGenerator;
import com.zhqc.framerwork.common.exception.ZhqcException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class ServiceControllerPlugin extends PluginAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServiceControllerPlugin.class);
    public boolean validate(List<String> warnings) {
        return true;
    }
    private static Configuration configuration = initFreemarkerConfiguration();

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        String javaRepositoryPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String javaMapperType = introspectedTable.getMyBatis3JavaMapperType();
        String basePackage = javaRepositoryPackage.substring(0, javaRepositoryPackage.lastIndexOf('.'));
        String javaClassName = javaMapperType.substring(javaMapperType.lastIndexOf('.') + 1, javaMapperType.length()).replace("Dao", "");
        String targetProject = this.getContext().getJavaClientGeneratorConfiguration().getTargetProject();
        Map<String, Object> root = getDataMapInit(introspectedTable,basePackage,javaClassName);
        genService(targetProject, basePackage, javaClassName, root);
        genServiceImpl(targetProject, basePackage, javaClassName, root);
        genController(targetProject, basePackage, javaClassName, root);
        String export = introspectedTable.getTableConfiguration().getProperties().getProperty("export");
        if("export".equals(export)){
            genExportServiceImpl(targetProject, basePackage, javaClassName, root);

            genExportTemplateService(targetProject,root);
        }
        return null;
    }

    private void genService(String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + "/service/" + javaClassName
                + "Service.java";
        File file = new File(filePath);
        // 查看父级目录是否存在, 不存在则创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            Template temp = configuration.getTemplate("service.ftl");
            writer = new FileWriter(file);
            temp.process(root,writer);
            logger.info(javaClassName + "Service.java 生成成功!");
        } catch (Exception e) {
            logger.error("genService case some error ",e);
            throw new ZhqcException(300,"加载FreeMaker模板文件失败");
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void genExportTemplateService(String targetProject,Map<String, Object> root) {
        String filePath = targetProject + "/template/export/" + root.get("modelNameLowerCamel")
                + "ExportService.xlsx";
        File file = new File(filePath);
        // 查看父级目录是否存在, 不存在则创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        String[] title = (String[]) root.get("exportTitle");
        String[][] content = new String[1][];
        content[0] = (String[]) root.get("exportField");
        XSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("Sheet1", title, content, null);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            wb.write(os);
            os.flush();
            os.close();
            logger.info(root.get("modelNameLowerCamel") + "Service.xlsx 生成成功!");
        } catch (Exception e) {
            logger.error("genExportTemplateService case some error ",e);
            throw new ZhqcException(300,"加载FreeMaker模板文件失败");
        }finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void genServiceImpl(String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + "/service/impl/" + javaClassName
                + "ServiceImpl.java";
        File file = new File(filePath);
        // 查看父级目录是否存在, 不存在则创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            Template temp = configuration.getTemplate("service-impl.ftl");
            writer = new FileWriter(file);
            temp.process(root, writer);
            logger.info(javaClassName + "ServiceImpl.java 生成成功!");
        } catch (Exception e) {
            logger.error("genServiceImpl case some error ",e);
            throw new ZhqcException(300,"加载FreeMaker模板文件失败");
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void genExportServiceImpl(String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + "/service/impl/" + javaClassName
                + "ExportServiceImpl.java";
        File file = new File(filePath);
        // 查看父级目录是否存在, 不存在则创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            Template temp = configuration.getTemplate("exportService-impl.ftl");
            writer = new FileWriter(file);
            temp.process(root, writer);
            logger.info(javaClassName + "ExportServiceImpl.java 生成成功!");
        } catch (Exception e) {
            logger.error("genExportServiceImpl case some error ",e);
            throw new ZhqcException(300,"加载FreeMaker模板文件失败");
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void genController(String targetProject, String topPackage, String javaClassName,
                               Map<String, Object> root) {
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + "/controller/" + javaClassName
                + "Controller.java";
        File file = new File(filePath);
        // 查看父级目录是否存在, 不存在则创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            Template temp = configuration.getTemplate("controller.ftl");
            writer = new FileWriter(file);
            temp.process(root, writer);
            logger.info(javaClassName + "Controller.java 生成成功!");
        } catch (Exception e) {
            logger.error("genController case some error ",e);
            throw new ZhqcException(300,"加载FreeMaker模板文件失败");
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Freemarker 模板环境配置
     */
    private static Configuration initFreemarkerConfiguration() {
        Configuration cfg = null;
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        try {
//            cfg.setDirectoryForTemplateLoading(new File("src/main/resources/template"));
            cfg.setClassForTemplateLoading(ZhqcGenerator.class,"/template");
        } catch (Exception e) {
            e.printStackTrace();
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return cfg;
    }

    private Map<String, Object> getDataMapInit(IntrospectedTable introspectedTable,String basePackage,String javeClassName) {
        Map<String, Object> data = new HashMap<>();
        data.put("baseRequestMapping", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, javeClassName));
        data.put("modelNameUpperCamel", javeClassName);
        data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, javeClassName));
        data.put("basePackage", basePackage);
        data.put("modelCode",javeClassName.toUpperCase());
        MyCommentGenerator myCommentGenerator = ((MyCommentGenerator)context.getCommentGenerator());
        String author = myCommentGenerator.getAuthor();
        String date = myCommentGenerator.getDateContent();
        String clientId = context.getProperties().getProperty("clientId");
        String generatorType = introspectedTable.getTableConfiguration().getProperties().getProperty("generatorType");
        data.put("generatorType",generatorType);
        String queryKey = introspectedTable.getTableConfiguration().getProperties().getProperty("queryKey");
        String[] queryKeys;
        JSONObject json;
        if(StringUtils.isNotBlank(queryKey)){
            queryKeys = queryKey.split("\\,");
        }else{
            queryKeys = new String[]{};
        }
        String primaryKey = introspectedTable.getTableConfiguration().getProperties().getProperty("primaryKey");
        String[] primaryKeys;
        if(StringUtils.isNotBlank(primaryKey)){
            primaryKeys = primaryKey.split("\\,");
        }else{
            primaryKeys = new String[]{};
        }
        List<JSONObject> primaryKeysUpper = new ArrayList<>();
        List<JSONObject> queryKeysUpper = new ArrayList<>();
        for(String str : queryKeys){
            json = new JSONObject();
            json.put("key",toUpperCaseFirstOne(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,str.split("\\&")[0])));
            json.put("condition",toUpperCaseFirstOne(str.split("\\&")[1]));
            json.put("type",str.split("\\&")[2]);
            queryKeysUpper.add(json);
        }
        List<String> primaryKeysLower = new ArrayList<>();
        for(String str : primaryKeys){
            json = new JSONObject();
            String[] temp = str.split("\\&");
            String key = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,temp[0]);
            primaryKeysLower.add(key);
            json.put("key",toUpperCaseFirstOne(key));
            json.put("type",temp[1]);
            primaryKeysUpper.add(json);
        }
        data.put("queryKeys",queryKeys);
        data.put("queryKeysUpper",queryKeysUpper);
        data.put("primaryKeys",primaryKeysLower);
        data.put("primaryKeysUpper",primaryKeysUpper);
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        List<String> primaryIds = new ArrayList<>();
        List<String> primaryIdsUpper = new ArrayList<>();
        for(IntrospectedColumn cl : primaryKeyColumns){
            primaryIds.add(cl.getJavaProperty());
            primaryIdsUpper.add(toUpperCaseFirstOne(cl.getJavaProperty()));
        }
        data.put("primaryIds",primaryIds);
        data.put("primaryIdsUpper",primaryIdsUpper);
        data.put("clientId",clientId);
        data.put("author",author);
        data.put("date",date);

        String exportName = introspectedTable.getTableConfiguration().getProperties().getProperty("export_name");
        String exportField = introspectedTable.getTableConfiguration().getProperties().getProperty("export_field");
        if(StringUtils.isNotBlank(exportField)){
            exportField = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,exportField);
            data.put("exportField",exportField.split("\\,"));
        }
        if(StringUtils.isNotBlank(exportName)){
            data.put("exportTitle",exportName.split("\\,"));
        }
        String remark = introspectedTable.getRemarks();
        if (StringUtils.isNotBlank(remark)) {
            data.put("remark",introspectedTable.getRemarks());
        }else{
            data.put("remark","表注释缺失,请按规范填写");
        }
        return data;
    }

    /**
     * 将字符串的第一位转为大写
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String toUpperCaseFirstOne(String str) {
        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        } else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
    }
}