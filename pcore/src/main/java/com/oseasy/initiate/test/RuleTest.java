package com.oseasy.initiate.test;

import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.common.utils.Rule;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/11.
 */
@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations = "classpath*:/spring-context*.xml")
public class RuleTest {
    @org.junit.Test
    public  void method() {
    Rule rule = new  Rule( "S",  "E",  "yyyyMMdd",  "HHmmss",  2,  3);

        System.out.println( IdUtils.genNumber(rule));
    }

    public static void main(String[] args )  throws ParseException {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date graduation=sdf.parse("2014-6-3");

            Date date=new Date();
            long day=(date.getTime()-graduation.getTime())/(24*60*60*1000) + 1;
            String year=new DecimalFormat("#").format(day/365f);
            System.out.println(year);



        }
    }


