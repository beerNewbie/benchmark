package com.hbc.cases;

import com.hbc.Case;
import com.hbc.annotation.Benchmark;
import com.hbc.annotation.Measurement;
import com.hbc.annotation.WarmUp;

import java.util.Arrays;
import java.util.Random;

/**
 * @Author: Beer
 * @Date: 2019/7/16 9:42
 * @Description:
 */
@WarmUp(time = 10)
@Measurement(iterations = 10, group = 3)
public class SortCase implements Case {
    //一路快排:
    public void quickSortOne(int[] a) {
        quickSortInternalOne(a,0,a.length-1);
    }
    private void quickSortInternalOne(int[] a, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partitionOne(a,low,high);

        //*****注意下标不要弄错*******
        quickSortInternalOne(a,low,pivotIndex-1);
        quickSortInternalOne(a,pivotIndex+1,high);
    }
    private int partitionOne(int[] a, int l, int r) {
        int randomIndex = (int) (Math.random()*(r-l+1)+l);
        swap(a,l,randomIndex);
        int v = a[l];
        //比基准值小的范围[l+1,j]
        int j = l;
        //比基准值大的范围[j+1,i-1]
        //索引从[i,r]
        int i = l+1;
        for (;i <= r; i++) {
            if (a[i] < v) {
                //将j+1与i交换位置，从而j++后使比基准值小的范围仍是[l+1,j]
                swap(a,j+1,i);
                j++;
            }
        }
        //此时j的位置元素是比基准值小的最右边一个，交换a[l]与a[j]的位置保证基准值a[l]处于正确位置
        swap(a,l,j);
        return j;
    }

    //二路快排
    public void quickSortTwo(int[] a) {
        quickSortInternalTwo(a,0,a.length-1);
    }
    private void quickSortInternalTwo(int[] a, int low, int high) {
        if (high <= low) {
            return;
        }

        int pivotIndex = partitionTwo(a,low,high);

        quickSortInternalTwo(a,low,pivotIndex-1);
        quickSortInternalTwo(a,pivotIndex+1,high);
    }
    private int partitionTwo(int[] a, int l, int r) {
        int randomIndex = (int) (Math.random()*(r-l+1)+l);
        swap(a,l,randomIndex);
        int v = a[l];
        int i = l+1;
        int j = r;
        while (true) {
            while (i <= r && a[i] < v) i++;
            while (j >= l + 1 && a[j] > v) j--;
            //判断跳出循环条件只能在循环里面swap()之前，否则swap会数组下标越界
            if (i > j) {
                break;
            }
            swap(a,i,j);
            i++;
            j--;
        }
        //此时a[j]是小于基准值最右边的一个数，因此将a[l]与a[j]交换
        swap(a,l,j);
        return j;
    }
    //三路快排
    public void quickSortThree(int[] a) {
        quickSortInternalThree(a,0,a.length-1);
    }
    private void quickSortInternalThree(int[] a, int low, int high) {
        if (high <= low) {
            return;
        }

        int[] pivotIndex = partitionThree(a,low,high);

        quickSortInternalThree(a,low,pivotIndex[0]-1);
        quickSortInternalThree(a,pivotIndex[1],high);
    }
    private int[] partitionThree(int[] a, int l, int r) {
        int randomIndex  = (int) (Math.random()*(r-l+1)+l);
        swap(a,l,randomIndex);
        int v = a[l];
        //小于基准值的范围[l+1,lt]
        //大于基准值的范围[gt,r]
        //等于基准值的范围[lt+1,i-1]
        int i = l+1;
        int lt = l;
        //注意gt=r+1;而不是r，因为下面交换的是a[gt-1]
        int gt = r+1;
        while (i < gt) {
            if (a[i] < v) {
                //此时交换的下标是lt+1,即将等于基准值的范围向右平移了一个单位
                swap(a,lt+1,i);
                lt++;
                i++;
            }else if (a[i] > v) {
                //此时交换的下标是gt-1，即将大于基准值的范围向左扩大了一个单位
                swap(a,gt-1,i);
                gt--;
            }else {
                i++;
            }
        }
        swap(a,l,lt);
        int[] patition = {lt,gt};
        return patition;
    }

    private void swap(int[] a, int indexA, int indexB) {
        int temp = a[indexA];
        a[indexA] = a[indexB];
        a[indexB] = temp;
    }

    /**
     * 归并排序
     * @param a
     */
    public void mergeSort(int[] a) {
        //设置为左闭右开
        mergeSortInternal(a,0,a.length);
    }
    private void mergeSortInternal(int[] a, int low, int high) {
        if (high <= low + 1) {
            return;
        }

        int mid = (low+high)>>1;

        mergeSortInternal(a,low,mid);
        mergeSortInternal(a,mid,high);

        merge(a,low,mid,high);
    }
    private void merge(int[] a, int low, int mid, int high) {
        int length = high - low;
        int[] extra = new int[length];

        int i = low;
        int j = mid;
        int k = 0;

        while (i < mid && j < high) {
            if (a[i] <= a[j]) {
                extra[k++] = a[i++];
            }else {
                extra[k++] = a[j++];
            }
        }
        //将剩余数据拷贝
        while (i < mid) {
            extra[k++] = a[i++];
        }
        while (j < high) {
            extra[k++] = a[j++];
        }
        System.arraycopy(extra,0,a,low,length);
    }

    @Benchmark
    public void testQuickOne() {
        int[] a = new int[100000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        quickSortOne(a);
    }
    @Benchmark
    public void testQuickTwo() {
        int[] a = new int[100000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        quickSortTwo(a);
    }
    @Benchmark
    public void testQuickThree() {
        int[] a = new int[100000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        quickSortThree(a);
    }
    @Benchmark
    public void testMergeSort() {
        int[] a = new int[100000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        mergeSort(a);
    }
    @Benchmark
    public void testArraysSort() {
        int[] a = new int[100000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        Arrays.sort(a);
    }
}
