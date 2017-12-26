package com.hch.platform.pcore.test;

import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzheng on 2017/2/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class StartScoreProcessTest {



    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    UserService userService;



    //启动流程
    @Test
    public void start() {

        List<String> teachers=getTeachers();
        Map<String,Object> vars=new HashMap<String,Object>();
        vars.put("teachers",teachers);
        for(String teacherId:teachers) {
            System.out.println(teacherId);
        }
        String key="multi_task";
        runtimeService.startProcessInstanceByKey(key,vars);
        for(String teacherId:teachers) {
            System.out.println(taskService.createTaskQuery().taskAssignee(teacherId).count());
        }



    }

    public List<String> getTeachers() {
        List<AbsUser> ts=userService.findListByRoleName("teachers");
        List<String> teachers=new ArrayList<String>();
        for (AbsUser user:ts) {
            teachers.add(user.getLoginName());
        }
        return teachers;
    }


}
