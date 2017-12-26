package com.hch.platform.pcore.test;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhangzheng on 2017/2/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class ProDefintIdTest {

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

    @Test
    public void test() {

      String ProcessDefinitionId=  getProcessDefinitionIdByProInstId("bfda9de7065a46a7b4fa14a020d15c84");
        System.out.println(ProcessDefinitionId);
    }

    public String getProcessDefinitionIdByProInstId(String proInstId) {
        List<HistoricTaskInstance> hisList=historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(proInstId).list();
        if (hisList.size()>0) {
            HistoricTaskInstance hisTaskIns=hisList.get(0);
            return hisTaskIns.getProcessDefinitionId();
        }else{
            return "";
        }
    }








}
