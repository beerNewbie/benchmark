package com.hbc;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author: Beer
 * @Date: 2019/7/16 9:40
 * @Description:
 */
public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException {
        CaseLoader caseLoader = new CaseLoader();
        caseLoader.load().run();

        //生成柱状图
        PrintHistogrom printHistogrom = new PrintHistogrom();
        JFrame frame=new JFrame("(预热后)性能评测表");
        frame.setLayout(new GridLayout(2,2,10,10));
        frame.add(printHistogrom.getChartPanel());   //添加柱形图
        frame.setBounds(0, 0, 900, 800);
        frame.setVisible(true);
    }
}
