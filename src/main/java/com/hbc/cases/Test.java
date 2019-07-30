package com.hbc.cases;

import java.util.Arrays;
import java.util.Random;

/**
 * @Author: Beer
 * @Date: 2019/7/16 15:51
 * @Description:
 */
public class Test {
    public static void main(String[] args) {
        SortCase sortCase = new SortCase();
        int[] arr1 = new int[1000000] ;
        int[] arr2 = new int[1000000];
        int[] arr3 = new int[1000000];
        int[] arr4 = new int[1000000];
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = new Random().nextInt(10000);
            arr2[i] = new Random().nextInt(10000);
            arr3[i] = new Random().nextInt(10000);
            arr4[i] = new Random().nextInt(10000);
            //System.out.println(arr1[i]+" "+arr2[i]+" "+arr3[i]+" "+arr4[i]);
        }
        long t1 = System.currentTimeMillis();
        sortCase.mergeSort(arr4);
        long t2 = System.currentTimeMillis();
        System.out.println("mergeSort耗时："+(t2-t1)+"ms");
        sortCase.quickSortOne(arr1);
        long t3 = System.currentTimeMillis();
        System.out.println("quick1耗时："+(t3-t2)+"ms");

        sortCase.quickSortTwo(arr2);
        long t4 = System.currentTimeMillis();
        System.out.println("quick2耗时："+(t4-t3)+"ms");

        sortCase.quickSortThree(arr3);
        long t5 = System.currentTimeMillis();
        System.out.println("quick3耗时："+(t5-t4)+"ms");
    }
}
