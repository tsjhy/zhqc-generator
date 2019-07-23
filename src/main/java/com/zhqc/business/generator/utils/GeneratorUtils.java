package com.zhqc.business.generator.utils;

import com.zhqc.business.generator.model.ao.TableConfigReq;
import com.zhqc.framerwork.common.exception.ZhqcException;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GeneratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(GeneratorUtils.class);
    public static void generator(Context context,List<String> tables){
        MyBatisGenerator myBatisGenerator = buildMyBatisGenerator(DataUtils.getTabConfigList(tables),context);
        try {
            myBatisGenerator.generate(null);
            context.getTableConfigurations().clear();
        } catch(ZhqcException e){
            throw e;
        }catch (Exception e) {
            logger.error("GeneratorUtils generate error ",e);
            throw new ZhqcException(300,"代码生成执行失败");
        }

    }
    public static Context buildContext(String clientId,String dbName,String author,String ip,String port,String userName,String pwd,String basePackage,String bashPath){

        Context context = new Context(null);
        context.setTargetRuntime("MyBatis3");
        context.setId("DB2Tables");
        //自动识别数据库关键字，默认false，如果设置为true，
        //根据SqlReservedWords中定义的关键字列表；一般保留默认值，遇到数据库关键字（Java关键字），
        //使用columnOverride覆盖
        context.addProperty(PropertyRegistry.CONTEXT_AUTO_DELIMIT_KEYWORDS,"true");

        //生成的Java文件的编码
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING,"utf-8");
        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");
        context.addProperty("clientId",clientId.toUpperCase());
        //格式化java代码
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FORMATTER,"org.mybatis.generator.api.dom.DefaultJavaFormatter");
        //格式化xml代码
        context.addProperty(PropertyRegistry.CONTEXT_XML_FORMATTER,"org.mybatis.generator.api.dom.DefaultXmlFormatter");
        //格式化信息
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
        pluginConfiguration.setConfigurationType("com.zhqc.business.generator.plugins.ServiceControllerPlugin");
        context.addPluginConfiguration(pluginConfiguration);
        //设置是否去除生成注释
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS,"true");
        commentGeneratorConfiguration.addProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE,"true");
        commentGeneratorConfiguration.addProperty("author",author);
        commentGeneratorConfiguration.setConfigurationType(MyCommentGenerator.class.getName());
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        //设置连接数据库
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL("jdbc:mysql://"+ip+":"+port+"/"+dbName+"?useInformationSchema=true");
        jdbcConnectionConfiguration.setPassword(pwd);
        jdbcConnectionConfiguration.setUserId(userName);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        //是否使用bigDecimal， false可自动转化以下类型（Long, Integer, Short, etc.）
        javaTypeResolverConfiguration.addProperty("forceBigDecimals","false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        //生成PO实体类的地址
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(basePackage+".model.po");
        javaModelGeneratorConfiguration.setTargetProject(bashPath);
        javaModelGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaModelGeneratorConfiguration.addProperty("trimStrings","true");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        //生成BO实体类的地址
        JavaBoGeneratorConfiguration javaBoGeneratorConfiguration = new JavaBoGeneratorConfiguration();
        javaBoGeneratorConfiguration.setTargetPackage(basePackage+".model.bo");
        javaBoGeneratorConfiguration.setTargetProject(bashPath);
        javaBoGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaBoGeneratorConfiguration.addProperty("trimStrings","true");
        context.setJavaBoGeneratorConfiguration(javaBoGeneratorConfiguration);

        //生成VO实体类的地址
        JavaVoGeneratorConfiguration javaVoGeneratorConfiguration = new JavaVoGeneratorConfiguration();
        javaVoGeneratorConfiguration.setTargetPackage(basePackage+".model.vo");
        javaVoGeneratorConfiguration.setTargetProject(bashPath);
        javaVoGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaVoGeneratorConfiguration.addProperty("trimStrings","true");
        context.setJavaVoGeneratorConfiguration(javaVoGeneratorConfiguration);

        //生成Query实体类的地址
        JavaQueryGeneratorConfiguration javaQueryGeneratorConfiguration = new JavaQueryGeneratorConfiguration();
        javaQueryGeneratorConfiguration.setTargetPackage(basePackage+".model.query");
        javaQueryGeneratorConfiguration.setTargetProject(bashPath);
        javaQueryGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaQueryGeneratorConfiguration.addProperty("trimStrings","true");
        javaQueryGeneratorConfiguration.addProperty("rootClass","com.zhqc.framerwork.common.model.query.BaseQuery");
        context.setJavaQueryGeneratorConfiguration(javaQueryGeneratorConfiguration);
        //生成Req实体类的地址
        JavaReqGeneratorConfiguration javaReqGeneratorConfiguration = new JavaReqGeneratorConfiguration();
        javaReqGeneratorConfiguration.setTargetPackage(basePackage+".model.ao");
        javaReqGeneratorConfiguration.setTargetProject(bashPath);
        javaReqGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaReqGeneratorConfiguration.addProperty("trimStrings","true");
        context.setJavaReqGeneratorConfiguration(javaReqGeneratorConfiguration);
        //生成UpdateReq实体类的地址
        JavaUpdateReqGeneratorConfiguration javaUpdateReqGeneratorConfiguration = new JavaUpdateReqGeneratorConfiguration();
        javaUpdateReqGeneratorConfiguration.setTargetPackage(basePackage+".model.ao");
        javaUpdateReqGeneratorConfiguration.setTargetProject(bashPath);
        javaUpdateReqGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaUpdateReqGeneratorConfiguration.addProperty("trimStrings","true");

        context.setJavaUpdateReqGeneratorConfiguration(javaUpdateReqGeneratorConfiguration);
        //生成的xml的地址
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(bashPath);
        sqlMapGeneratorConfiguration.setTargetPackage("mapping");
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages","true");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
        //生成DAO接口
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(basePackage+".dao");
        javaClientGeneratorConfiguration.setTargetProject(bashPath);
        //注解形式 ANNOTATEDMAPPER xml形式 XMLMAPPER
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.addProperty("enableSubPackages","true");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        return context;
    }
    public static MyBatisGenerator buildMyBatisGenerator(List<TableConfigReq> configList, Context context){
        //配置xml配置项
        List<String> warnings = new ArrayList<String>();
        Configuration config = new Configuration();

        TableConfiguration tableConfiguration;
        for(TableConfigReq req : configList){
            tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(req.getTableName());
            tableConfiguration.addProperty("generatorType",req.getGeneratorType());
            if(req.isUseGeneratedKeys()){
                String primaryId = req.getPrimaryField();
                GeneratedKey generatedKey = new GeneratedKey(primaryId,"JDBC",false,"");
                tableConfiguration.setGeneratedKey(generatedKey);
            }
            tableConfiguration.setDomainObjectName(req.getDomainName());
            if(StringUtils.isNotBlank(req.getQueryKey())){
                tableConfiguration.addProperty("queryKey",req.getQueryKey());
            }
            if(StringUtils.isNotBlank(req.getQueryRequiredField())){
                tableConfiguration.addProperty("query_requiredField",req.getQueryRequiredField());
            }
            if(StringUtils.isNotBlank(req.getQueryHiddenApiField())){
                tableConfiguration.addProperty("query_hiddenApiField",req.getQueryHiddenApiField());
            }
            if(StringUtils.isNotBlank(req.getQueryHiddenValidateField())){
                tableConfiguration.addProperty("query_hiddenValidateField",req.getQueryHiddenValidateField());
            }
            if(StringUtils.isNotBlank(req.getBoRequiredField())){
                tableConfiguration.addProperty("bo_requiredField",req.getBoRequiredField());
            }
            if(StringUtils.isNotBlank(req.getPrimaryKey())){
                tableConfiguration.addProperty("primaryKey",req.getPrimaryKey());
            }
            if(StringUtils.isNotBlank(req.getAddRequiredField())){
                tableConfiguration.addProperty("add_requiredField",req.getAddRequiredField());
            }
            if(StringUtils.isNotBlank(req.getAddHiddenApiField())){
                tableConfiguration.addProperty("add_hiddenApiField",req.getAddHiddenApiField());
            }
            if(StringUtils.isNotBlank(req.getAddHiddenValidateField())){
                tableConfiguration.addProperty("add_hiddenValidateField",req.getAddHiddenValidateField());
            }
            if(StringUtils.isNotBlank(req.getEditRequiredField())){
                tableConfiguration.addProperty("edit_requiredField",req.getEditRequiredField());
            }
            if(StringUtils.isNotBlank(req.getEditHiddenApiField())){
                tableConfiguration.addProperty("edit_hiddenApiField",req.getEditHiddenApiField());
            }
            if(StringUtils.isNotBlank(req.getEditHiddenValidateField())){
                tableConfiguration.addProperty("edit_hiddenValidateField",req.getEditHiddenValidateField());
            }
            if(StringUtils.isNotBlank(req.getVoRequiredField())){
                tableConfiguration.addProperty("vo_requiredField",req.getVoRequiredField());
            }
            if(req.isShowExport()){
                tableConfiguration.addProperty("export","export");
                if(StringUtils.isNotBlank(req.getExportField())){
                    tableConfiguration.addProperty("export_field",req.getExportField());
                }
                if(StringUtils.isNotBlank(req.getExportName())){
                    tableConfiguration.addProperty("export_name",req.getExportName());
                }else{
                    tableConfiguration.addProperty("export_name",req.getExportField());
                }
            }
            context.addTableConfiguration(tableConfiguration);
        }
        config.addContext(context);
        try{
            DefaultShellCallback callback = new DefaultShellCallback(true);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            return myBatisGenerator;
        }catch(Exception e){
            logger.error("case some error",e);
        }
        return null;
    }

    public static void generatorBySql(Context context, String sqlTable) {
        MyBatisGenerator myBatisGenerator = buildMyBatisGeneratorBySql(DataUtils.getTableConfigReq(sqlTable),context);
        try {
            myBatisGenerator.generate(null);
            context.getTableConfigurations().clear();
        } catch(ZhqcException e){
            throw e;
        }catch (Exception e) {
            logger.error("GeneratorUtils generatorBySql error ",e);
            throw new ZhqcException(300,"代码生成执行失败");
        }
    }

    private static MyBatisGenerator buildMyBatisGeneratorBySql(TableConfigReq req, Context context) {
        {
            context.setJavaReqGeneratorConfiguration(null);
            context.setJavaUpdateReqGeneratorConfiguration(null);

            //配置xml配置项
            List<String> warnings = new ArrayList<String>();
            Configuration config = new Configuration();
            TableConfiguration tableConfiguration;
            tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(req.getTableName());

            tableConfiguration.setDomainObjectName(req.getDomainName());
            tableConfiguration.addProperty("generatorType",req.getGeneratorType());
            if(StringUtils.isNotBlank(req.getQueryKey())){
                tableConfiguration.addProperty("queryKey",req.getQueryKey());
            }
            if(StringUtils.isNotBlank(req.getQueryRequiredField())){
                tableConfiguration.addProperty("query_requiredField",req.getQueryRequiredField());
            }
            if(StringUtils.isNotBlank(req.getQueryHiddenApiField())){
                tableConfiguration.addProperty("query_hiddenApiField",req.getQueryHiddenApiField());
            }
            if(StringUtils.isNotBlank(req.getQueryHiddenValidateField())){
                tableConfiguration.addProperty("query_hiddenValidateField",req.getQueryHiddenValidateField());
            }
            if(StringUtils.isNotBlank(req.getBoRequiredField())){
                tableConfiguration.addProperty("bo_requiredField",req.getBoRequiredField());
            }
            if(StringUtils.isNotBlank(req.getVoRequiredField())){
                tableConfiguration.addProperty("vo_requiredField",req.getVoRequiredField());
            }
            if(StringUtils.isNotBlank(req.getPoRequiredField())){
                tableConfiguration.addProperty("po_requiredField",req.getPoRequiredField());
            }
            if(StringUtils.isNotBlank(req.getPoFieldRemark())){
                tableConfiguration.addProperty("po_fieldRemark",req.getPoFieldRemark());
            }
            if(StringUtils.isNotBlank(req.getPoFieldType())){
                tableConfiguration.addProperty("po_fieldType",req.getPoFieldType());
            }
            if(req.isShowExport()){
                tableConfiguration.addProperty("export","export");
                if(StringUtils.isNotBlank(req.getExportField())){
                    tableConfiguration.addProperty("export_field",req.getExportField());
                }
                if(StringUtils.isNotBlank(req.getExportName())){
                    tableConfiguration.addProperty("export_name",req.getExportName());
                }else{
                    tableConfiguration.addProperty("export_name",req.getExportField());
                }
                tableConfiguration.addProperty("exportSql",req.getExportSql());

            }
            context.addTableConfiguration(tableConfiguration);
            config.addContext(context);
            try{
                DefaultShellCallback callback = new DefaultShellCallback(true);
                MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
                return myBatisGenerator;
            }catch(Exception e){
                logger.error("buildMyBatisGeneratorBySql case some error",e);
            }
            return null;
        }
    }
}
