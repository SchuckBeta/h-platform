package com.hch.platform.pcore.test;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
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
public class ProcessTest {

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

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                                                        .latestVersion().active();
        processDefinitionQuery.processDefinitionKey("multi_task");
        List<ProcessDefinition> listProcess= processDefinitionQuery.list();
        String processDefinitionId="";
        for (ProcessDefinition processDefinition:listProcess) {
            if ("multi_task".equals(processDefinition.getKey())) {
                processDefinitionId=processDefinition.getId();
            }

        }

        BpmnModel bpmnModel =repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes=bpmnModel.getProcesses();
        Process process=processes.get(0);
        //获取所有的FlowElement信息
        Collection<FlowElement> flowElements = process.getFlowElements();

        for (FlowElement flowElement : flowElements) {
            //如果是任务节点
            if (flowElement instanceof UserTask) {
                UserTask userTask=(UserTask) flowElement;

                //获取入线信息
                List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
                SequenceFlow incomingFlow=incomingFlows.get(0);
                if (incomingFlow.getSourceRef().contains("start")) {
                    System.out.println(userTask.getAssignee()) ;
                }


            }
        }

    }








}
