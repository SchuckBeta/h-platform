package com.oseasy.initiate.test;

import org.activiti.engine.*;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzheng on 2017/2/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class EndScoreProcessTest {

    @Autowired
    private ProcessEngineFactoryBean processEngineFactory;

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
    public void end() {
        List<String> teachers=getTeachers();
        //遍历teachers ，获得各个teacher的任务，并完成任务
        for(String teacher:teachers) {
            TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(teacher);
            System.out.println(teacher+"的任务数量是："+todoTaskQuery.count());
            if (todoTaskQuery.list().size()==1) {
                Task task=todoTaskQuery.list().get(0);
                System.out.println(teacher+"开始完成评分,任务号是："+task.getId());
                taskService.complete(task.getId());
            }
        }


    }


    public List<String> getTeachers() {
        List<User> ts=userService.findListByRoleName("teachers");
        List<String> teachers=new ArrayList<String>();
        for (User user:ts) {
            teachers.add(user.getLoginName());
        }
        return teachers;
    }



}
