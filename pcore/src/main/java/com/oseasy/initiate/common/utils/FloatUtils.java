package com.oseasy.initiate.common.utils;

/**
 * Created by zhangzheng on 2017/3/16.
 */
public class FloatUtils {
    /**
     * 除法四舍五入 获得一位float
     * @param total
     * @param number
     * @return
     */
    public static float division(float total, int number) {
        float  a=total/(float) number;
        float   result   =   (float)(Math.round(a*10))/10;
        return result;
    }

    /**
     * 获取float 的整数部分
     * @param f
     * @return
     */
    public static int getInt(float f) {
        int i=0;
        i=(int)f;
        return i;
    }

    /**
     * 获取1位float 的小数部分
     * @param f
     * @return
     */
    public static int getPoint(float f) {
        int i=(int)f;
        float res= f-i;
        int point=(int)(res*10);
        return point;
    }

    public static void main(String[] args) {
        float f=1.45f;
        System.out.println(getPoint(f));
    }



}
