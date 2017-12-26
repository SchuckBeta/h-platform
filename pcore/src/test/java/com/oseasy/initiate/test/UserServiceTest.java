package com.hch.platform.pcore.test;

import com.hch.platform.pcore.modules.act.dao.ActDao;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.sys.service.UserService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangzheng on 2017/2/23.
 * 国创大赛后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-scheduler.xml","classpath:spring-context-shiro.xml","classpath:spring-mvc.xml"})
public class UserServiceTest {


    @Autowired
    private UserService userService;

    @Test
    public void test1() {
        List<String> secs=userService.getCollegeSecs("51e8ccdf3cc54625bbd6535b598bfa48");
        System.out.println(secs);
    }

    @Test
    public void test2() {
        List<String> secs=userService.getCollegeExperts("51e8ccdf3cc54625bbd6535b598bfa48");
        System.out.println(secs);
    }

    @Test
    public void test3() {
        List<String> secs=userService.getSchoolSecs();
        System.out.println(secs);
    }

    @Test
    public void test4() {
        List<String> secs=userService.getSchoolExperts();
        System.out.println(secs);
    }



}
