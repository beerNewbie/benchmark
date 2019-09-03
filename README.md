# 性能测试框架
### 项目介绍:
> 该项目的功能是对待测试程序进行性能分析，得出其运行指定次数所需的时间。其设计是模仿JMH进行设计，首先是通过反射和注解标记待测试方法、实现多级配置外，还能够使用接口标记待测试类，自动加载测试用例，生成性能评测柱状图。
## 性能测试框架结果展示：

#### 百万级数据下几种排序算法测试结果：

```java
//归并排序
testMergeSort经过10次预热耗时：1863126000纳秒
第1组试验耗时：1927675800纳秒
第2组试验耗时：1910187600纳秒
第3组试验耗时：1626011200纳秒
//优化后的快排
testQuickThree经过10次预热耗时：1022441900纳秒
第1组试验耗时：931091600纳秒
第2组试验耗时：943736800纳秒
第3组试验耗时：942599600纳秒
//快排的常规写法
testQuickTwo经过10次预热耗时：1169772900纳秒
第1组试验耗时：1139745900纳秒
第2组试验耗时：1140604600纳秒
第3组试验耗时：1177227200纳秒
```

![](https://github.com/beerNewbie/benchmark/blob/master/z_image/%E6%80%A7%E8%83%BD%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C%E7%A4%BA%E4%BE%8B%E5%9B%BE.png)



#### 待测试程序：

```java
package com.hbc.cases;

import com.hbc.Case;
import com.hbc.annotation.Benchmark;
import com.hbc.annotation.Measurement;
import com.hbc.annotation.WarmUp;

import java.util.Arrays;
import java.util.Random;

/**
 * @WarmUp(time = 10)
 *      -->  预热进行的次数10次
 * @Measurement(iterations = 10, group = 3)
 *      -->   待测试程序进行3组测试，每组执行10次
 */

@WarmUp(time = 10)
@Measurement(iterations = 10, group = 3)
public class SortCase implements Case {


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
    public void testQuickTwo() {
        int[] a = new int[1000000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        quickSortTwo(a);
    }
    @Benchmark
    public void testQuickThree() {
        int[] a = new int[1000000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        quickSortThree(a);
    }
    @Benchmark
    public void testMergeSort() {
        int[] a = new int[1000000];
        Random random = new Random(20190716);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        mergeSort(a);
    }

}

```

