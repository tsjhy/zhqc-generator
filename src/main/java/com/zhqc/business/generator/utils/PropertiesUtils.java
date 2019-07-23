package com.zhqc.business.generator.utils;

import com.zhqc.business.generator.ZhqcGenerator;
import com.zhqc.business.generator.model.GeneratorConfig;
import com.zhqc.framerwork.common.exception.ZhqcException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;


public class PropertiesUtils {
    private static Logger logger = Logger.getLogger(PropertiesUtils.class);
    public static Properties getProperty(String fileName) {
        Properties props = new Properties();
        try {
            String configPaht = System.getProperty("user.dir");
            InputStream input = new FileInputStream(configPaht+"\\"+fileName);
            logger.info("读取配置文件："+configPaht+"\\"+fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(input));
            props.load(bf);
        } catch (IOException e) {
            try {
                logger.info("读取默认配置文件");
                props.load(new BufferedReader(new InputStreamReader(ZhqcGenerator.class.getClassLoader().getResourceAsStream(fileName))));
            } catch (IOException e1) {
                throw new ZhqcException(300,"配置文件读取失败");
            }
        }
        return props;
    }
    public static GeneratorConfig getConfig(String fileName){
        GeneratorConfig config = new GeneratorConfig();
        Properties properties = getProperty(fileName);
        String author = properties.getProperty("author");
        if(StringUtils.isNotBlank(author)){
            config.setAuthor(author);
        }
        String basePackage = properties.getProperty("basePackage");
        if(StringUtils.isNotBlank(basePackage)){
            config.setBasePackage(basePackage);
        }
        String basePath = properties.getProperty("basePath");
        if(StringUtils.isNotBlank(basePath)){
            config.setBasePath(basePath);
        }
        String clientId = properties.getProperty("clientId");
        if(StringUtils.isNotBlank(clientId)){
            config.setClientId(clientId);
        }
        String database = properties.getProperty("database");
        if(StringUtils.isNotBlank(database)){
            config.setDatabase(database);
        }
        String host = properties.getProperty("host");
        if(StringUtils.isNotBlank(host)){
            config.setHost(host);
        }
        String port = properties.getProperty("port");
        if(StringUtils.isNotBlank(port)){
            config.setPort(port);
        }
        String password = properties.getProperty("password");
        if(StringUtils.isNotBlank(password)){
            config.setPassword(password);
        }
        String userName = properties.getProperty("username");
        if(StringUtils.isNotBlank(userName)){
            config.setUsername(userName);
        }
        return config;
    }
    public static String setProperty(String fileName, Map<String,String> config) {
        String message = "true";
        try {
            Properties props = getProperty(fileName);
            if (config != null) {
                Iterator<Entry<String, String>> iter = config.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    props.setProperty(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            OutputStream out = new FileOutputStream(fileName);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            props.store(writer, null);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("保存配置文件失败",e);
            throw new ZhqcException(300,"配置文件保存失败");
        }

        return message;
    }


    public static void main22(String[] args) {
        System.out.println(PropertiesUtils.getProperty("D:\\generator\\generator.properties"));
        Map<String, String> data = new HashMap<String, String>();
        data.put("author", "test");
        data.put("clientId", "test22");
        PropertiesUtils.setProperty("D:\\generator\\generator.properties", data);
    }

}