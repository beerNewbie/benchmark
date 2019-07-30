package com.hbc;

import com.hbc.annotation.Benchmark;
import com.hbc.annotation.Measurement;
import com.hbc.annotation.WarmUp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author: Beer
 * @Date: 2019/7/15 20:00
 * @Description:
 */

class CaseRunner {
    private static final int DEFAULT_ITERATIONS = 10;
    private static final int DEFAULT_GROUP = 5;
    private static final int DEFAULT_TIME = 5;
    private final List<Case> caseList;

    public CaseRunner(List<Case> caseList) {
        this.caseList = caseList;
    }

    public void run() throws InvocationTargetException, IllegalAccessException {
        for (Case benchCase : caseList) {
            int iterations = DEFAULT_ITERATIONS;
            int group = DEFAULT_GROUP;
            int time = DEFAULT_TIME;

            //获取WarmUp类级别的配置
            WarmUp classWarmUp = benchCase.getClass().getAnnotation(WarmUp.class);
            if (classWarmUp != null) {
                time = classWarmUp.time();
            }

            //获取类级别的配置
            Measurement classMeasurement = benchCase.getClass().getAnnotation(Measurement.class);
            if (classMeasurement != null) {
                iterations = classMeasurement.iterations();
                group = classMeasurement.group();
            }

            //通过注解找出需要测试的方法
            Method[] methods = benchCase.getClass().getMethods();
            for (Method method : methods) {
                Benchmark benchmark = method.getAnnotation(Benchmark.class);
                if (benchmark == null) {
                    continue;
                }

                //获取WarmUp方法级别的配置
                WarmUp methodWarmUp = method.getAnnotation(WarmUp.class);
                if (methodWarmUp != null) {
                    time = methodWarmUp.time();
                }

                //获取方法中的配置
                Measurement methodMeasurement = method.getAnnotation(Measurement.class);
                if (methodMeasurement != null) {
                    iterations = methodMeasurement.iterations();
                    group = methodMeasurement.group();
                }
                if (time != 0) {
                    warmUpCase(benchCase, method, time);
                }
                runCase(benchCase, method, iterations, group);
            }
        }

    }

    private void warmUpCase(Case benchCase, Method method, int time) throws InvocationTargetException, IllegalAccessException {
        System.out.print(method.getName());
        long t1 = System.nanoTime();
        for (int i = 0; i < time; i++) {
            method.invoke(benchCase);
        }
        long t2 = System.nanoTime();
        System.out.println("经过" + time + "次预热耗时：" + (t2 - t1) + "纳秒");
    }

    private void runCase(Case benchCase, Method method, int iterations, int group) throws InvocationTargetException, IllegalAccessException {
        System.out.println(method.getName()+"：共测试"+group+"组，每组执行"+iterations+"次");
        for (int i = 0; i < group; i++) {
            long t1 = System.nanoTime();
            for (int j = 0; j < iterations; j++) {
                method.invoke(benchCase);
            }
            long t2 = System.nanoTime();
            System.out.println("第" + (i + 1) + "组试验耗时：" + (t2 - t1) + "纳秒");
        }
    }
}

public class CaseLoader {
    public CaseRunner load() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String pkgDot = "com.hbc.cases";
        String pkg = "com/hbc/cases";
        List<String> classNameList = new ArrayList<String>();

        //1.根据固定类找到类加载器
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> urls = classLoader.getResources(pkg);
        //2.根据类加载器找到文件所在的路径
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            //不是*.class文件，则不支持
            if (!url.getProtocol().equals("file")) {
                continue;
            }

            String dirName = URLDecoder.decode(url.getPath(), "UTF-8");
            File dir = new File(dirName);
            //判断是否是目录
            if (!dir.isDirectory()) {
                continue;
            }
            //3.扫描该目录下的的所有*.class文件,作为所有的类文件
            File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                //判断后缀是否是class文件
                String fileName = file.getName();
                String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (!prefix.equals("class")) {
                    continue;
                }
                String className = fileName.substring(0, fileName.length() - 6);
                classNameList.add(className);
            }

        }

        List<Case> caseList = new ArrayList<Case>();
        for (String className : classNameList) {
            Class<?> cls = Class.forName(pkgDot + "." + className);
            if (Case.class.isAssignableFrom(cls)) {
                caseList.add((Case) cls.newInstance());
            }
        }

        return new CaseRunner(caseList);
    }
}
