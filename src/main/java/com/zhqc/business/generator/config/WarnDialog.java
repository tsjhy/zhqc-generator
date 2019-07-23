package com.zhqc.business.generator.config;

import javax.swing.*;

public class WarnDialog {
    public static void showWarnDialog(String content, JFrame parentFrame){
        JOptionPane.showMessageDialog(parentFrame, content, "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void showInfoDialog(String content, JFrame parentFrame){
        JOptionPane.showMessageDialog(parentFrame, content, "信息",JOptionPane.INFORMATION_MESSAGE);
    }
}
