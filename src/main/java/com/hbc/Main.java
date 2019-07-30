package com.hbc;

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
    }
}
