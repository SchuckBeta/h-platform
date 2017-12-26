package com.hch.platform.pcore.test;

import com.hch.platform.pcore.modules.act.dao.ActDao;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.act.service.ActTaskService;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.UserService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangzheng on 2017/2/23.
 * 国创大赛后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class ProjectProcessTest {

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
    private ActDao actDao;

    @Autowired
    UserService userService;

    @Autowired
    ProjectDeclareDao projectDeclareDao;

    @Autowired
    UserDao userDao;
    @Autowired
    ProjectDeclareService projectDeclareService;

    @Autowired
    ActTaskService actTaskService;




    //模拟一个学生（stu3)提交国创项目
    @Test
    public void start() {
        ProjectDeclare projectDeclare=projectDeclareDao.get("zztest3");
        projectDeclareService.startPojectProcess(projectDeclare);
    }


    //模拟一个学生（stu2)提交国创项目
    @Test
    public void start5() {
        //启动国创项目流程
        identityService.setAuthenticatedUserId("stu2"); //说明stu2启动
        String procDefKey="state_project_audit";
        String businessTable="project_declare";
        String businessId="zztest5";
        HashMap<String,Object> vars=new HashMap<String,Object>();
        vars.put("collegeSec","physicalSec1,physicalSec2"); //给谁审批  固定给physicalSec1、physicalSec2审批
        vars.put("number","201610511004");  // 项目编号
        vars.put("id","zztest5");  // 项目编号
        vars.put("name","大学章程制度的法律分析：起源、现实和绩效"); //项目名称
        vars.put("type","3");  //项目类型
        vars.put("leader","薛晓丹");  //项目负责人
        vars.put("leaderNumber","2014210308");  // 学号
        vars.put("teamList","覃晶晶/2014210274,赵心欣/2014210276,方帅/2014210313");  // 项目组成员
        vars.put("teacher","常健");  // 指导老师


        ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable+":"+businessId, vars);

        Act act = new Act();
        act.setBusinessTable(businessTable);// 业务表名
        act.setBusinessId(businessId);	// 业务表ID
        act.setProcInsId(procIns.getId());
        actDao.updateProcInsIdByBusinessId(act);

    }



    @Test
    public void getTaskId() {
        String procInsId="f68fff2a4a6344d6ac79b92f6f6a8a7c";
        List<HistoricTaskInstance> hisList=historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
        if (hisList.size()>0) {
            HistoricTaskInstance hisTaskIns=hisList.get(0);
            System.out.println("taskId--"+hisTaskIns.getId());
            System.out.println("procDefId--"+hisTaskIns.getProcessDefinitionId());
            System.out.println("taskName--"+hisTaskIns.getName());
        }else{

        }
    }










}
