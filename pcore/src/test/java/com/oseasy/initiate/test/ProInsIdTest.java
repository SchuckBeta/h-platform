package com.oseasy.initiate.test;

import com.oseasy.initiate.common.utils.StringUtil;
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
public class ProInsIdTest {

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
        String taskDefKey="audit2";
        String processDefinitionKey="multi_task";
        String next=getNextRoleName(taskDefKey,processDefinitionKey);
        System.out.println("下一个:"+next);

    }


    public String getNextRoleName(String taskDefKey,String processDefinitionKey) {
        String assignee=""; //${teacher}
        String nextTaskDefKey="";
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion().active();
        processDefinitionQuery.processDefinitionKey(processDefinitionKey);
        List<ProcessDefinition> listProcess= processDefinitionQuery.list();
        String processDefinitionId=listProcess.get(0).getId();

        BpmnModel bpmnModel =repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes=bpmnModel.getProcesses();
        Process process=processes.get(0);
        //获取所有的FlowElement信息
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            //如果是任务节点
            if (flowElement instanceof UserTask) {

                UserTask userTask=(UserTask) flowElement;

                if (taskDefKey.equals(userTask.getId())) { //如果是当前节点，获取下个节点的名称
                    List<SequenceFlow> outGoingFlows=  userTask.getOutgoingFlows();
                    SequenceFlow  outGoingFlow=outGoingFlows.get(0);
                    if ( outGoingFlow.getTargetRef().contains("audit")) {
                        nextTaskDefKey=outGoingFlow.getTargetRef();
                        break;
                    }
                }

            }
        }

        if (StringUtil.isNotBlank(nextTaskDefKey)) {
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof UserTask) {
                    UserTask userTask=(UserTask) flowElement;
                    if (nextTaskDefKey.equals(userTask.getId())) {
                        assignee=userTask.getAssignee();
                    }
                }
            }

        }

        if (StringUtil.isNotBlank(assignee)) {
            String[] roleNames =assignee.split("\\}");
            String roleName0=roleNames[0];
            String[] realName= roleName0.split("\\{");

            return realName[1];
        }else{
            return "";
        }

    }







}
