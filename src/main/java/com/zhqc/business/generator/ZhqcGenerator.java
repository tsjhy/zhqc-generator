package com.zhqc.business.generator;

import com.zhqc.business.generator.config.MainFrame;
import com.zhqc.business.generator.model.GeneratorConfig;
import com.zhqc.business.generator.utils.PropertiesUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class ZhqcGenerator {
    private static Logger logger = Logger.getLogger(ZhqcGenerator.class);
    public static void main(String[] args) {
        Properties props=new Properties();
        try {
            props.load(ZhqcGenerator.class.getClassLoader().getResourceAsStream("log4j.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(900,700);
//        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        GeneratorConfig config = null;
        try{
            config = PropertiesUtils.getConfig("generator.properties");
        }catch (Exception e){
            logger.error("start case some error",e);
        }
        if(config != null){
            frame.initConfig(config);
        }
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int valuex= JOptionPane.showConfirmDialog(frame, "您确定要退出程序？", "温馨提示", JOptionPane.YES_NO_OPTION);
                if(valuex == JOptionPane.YES_OPTION){
                    Map<String,String> config = frame.getConfig();
                    PropertiesUtils.setProperty("generator.properties",config);
                    System.exit(0);
                }
            }
        });
    }
}
