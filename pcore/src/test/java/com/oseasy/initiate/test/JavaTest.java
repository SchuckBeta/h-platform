package com.oseasy.initiate.test;

import com.oseasy.initiate.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzheng on 2017/3/7.
 */
public class JavaTest {
    public static void main(String[] args) {
        List<String> masterList=new ArrayList<String>();
        List<String> studentList=new ArrayList<String>();
        masterList.add("1");
        masterList.add("2");
        studentList.add("3");
        studentList.add("4");
        List<String> masterAndStu=new ArrayList<String>();
        masterAndStu.addAll(studentList);
        masterAndStu.addAll(masterList);
        String oaNotifyRecordIds= StringUtil.join(masterAndStu.iterator(),",");
        System.out.println(oaNotifyRecordIds);
    }

}
