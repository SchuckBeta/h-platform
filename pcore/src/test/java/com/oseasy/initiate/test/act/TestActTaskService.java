/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.hch.platform.pcore.test.act
 * @Description [[_TestActTaskService_]]文件
 * @date 2017年6月7日 上午11:40:01
 *
 */

package com.hch.platform.pcore.test.act;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricDetailQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.activiti.validation.ValidationError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO 添加类/接口功能描述.
 * @author chenhao
 * @date 2017年6月7日 上午11:40:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-*.xml", "classpath:activiti.cfg.xml"})
//@ContextConfiguration(locations = {"classpath:activiti.cfg.xml"})
//@ContextConfiguration(locations = {"classpath:spring-*.xml", "file:src/main/webapp/WEB-INF/*.xml"})
//public class TestActTaskService extends AbstractTransactionalJUnit4SpringContextTests{
public class TestActTaskService extends SpringActivitiTestCase{
//public class TestActTaskService {

  @Rule
  public ActivitiRule activitiRule = new ActivitiRule();

  @Test
  public void startProcess() throws Exception {
    //流程服务
    RepositoryService repositoryService = activitiRule.getRepositoryService();
    // 流程运行服务
    RuntimeService runtimeService = activitiRule.getRuntimeService();
    // 流程表单服务
    FormService formService = activitiRule.getFormService();
    // 流程历史服务
    HistoryService historyService = activitiRule.getHistoryService();
    // 流程任务服务
    TaskService taskService = activitiRule.getTaskService();

    System.out.println(repositoryService);

    String jsonXml = "{\"resourceId\":\"4d32348b796e494299c37ddf342c5d5b\",\"properties\":{\"process_id\":\"process110\",\"name\":\"大赛流程110\",\"documentation\":\"\",\"process_author\":\"\",\"process_version\":\"\",\"process_namespace\":\"http://www.activiti.org/processdef\",\"executionlisteners\":\"{\\\"executionListeners\\\":\\\"[]\\\"}\",\"eventlisteners\":\"{\\\"eventListeners\\\":\\\"[]\\\"}\",\"signaldefinitions\":\"\\\"[]\\\"\",\"messagedefinitions\":\"\\\"[]\\\"\",\"messages\":[]},\"stencil\":{\"id\":\"BPMNDiagram\"},\"childShapes\":[{\"resourceId\":\"sid-E76260EF-151A-49A4-B235-825E54BAFF5B\",\"properties\":{\"overrideid\":\"sid-E76260EF-151A-49A4-B235-825E54BAFF5B\",\"name\":\"学生（项目负责人）\",\"documentation\":\"\",\"executionlisteners\":\"\",\"initiator\":\"\",\"formkeydefinition\":\"\",\"formproperties\":\"\"},\"stencil\":{\"id\":\"StartNoneEvent\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-FDE6ECD4-B683-434B-BC05-3BAF38B80B36\"}],\"bounds\":{\"lowerRight\":{\"x\":174.3333282470703,\"y\":115},\"upperLeft\":{\"x\":144.3333282470703,\"y\":85}},\"dockers\":[]},{\"resourceId\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\",\"properties\":{\"overrideid\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\",\"name\":\"学院审核（教学秘书）\",\"documentation\":\"\",\"asynchronousdefinition\":false,\"exclusivedefinition\":true,\"executionlisteners\":{\"executionListeners\":[]},\"multiinstance_type\":\"None\",\"multiinstance_cardinality\":\"\",\"multiinstance_collection\":\"\",\"multiinstance_variable\":\"\",\"multiinstance_condition\":\"\",\"isforcompensation\":\"false\",\"usertaskassignment\":\"\",\"formkeydefinition\":\"\",\"duedatedefinition\":\"\",\"prioritydefinition\":\"\",\"formproperties\":\"\",\"tasklisteners\":{\"taskListeners\":[]}},\"stencil\":{\"id\":\"UserTask\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-85C8FBB6-4298-4974-B2C6-F233D7543396\"}],\"bounds\":{\"lowerRight\":{\"x\":400,\"y\":140},\"upperLeft\":{\"x\":300,\"y\":60}},\"dockers\":[]},{\"resourceId\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\",\"properties\":{\"overrideid\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\",\"name\":\"审核结束（项目评级）\",\"documentation\":\"\",\"executionlisteners\":\"\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":645.3333282470703,\"y\":328},\"upperLeft\":{\"x\":617.3333282470703,\"y\":300}},\"dockers\":[]},{\"resourceId\":\"sid-FDE6ECD4-B683-434B-BC05-3BAF38B80B36\",\"properties\":{\"overrideid\":\"sid-FDE6ECD4-B683-434B-BC05-3BAF38B80B36\",\"name\":\"提交审核\",\"documentation\":\"\",\"conditionsequenceflow\":\"\",\"executionlisteners\":\"\",\"defaultflow\":\"false\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\"}],\"bounds\":{\"lowerRight\":{\"x\":299.8437486886978,\"y\":100},\"upperLeft\":{\"x\":175.22916197776794,\"y\":100}},\"dockers\":[{\"x\":15,\"y\":15},{\"x\":50,\"y\":40}],\"target\":{\"resourceId\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\"}},{\"resourceId\":\"sid-85C8FBB6-4298-4974-B2C6-F233D7543396\",\"properties\":{\"overrideid\":\"sid-85C8FBB6-4298-4974-B2C6-F233D7543396\",\"name\":\"提交给学校\",\"documentation\":\"\",\"conditionsequenceflow\":\"\",\"executionlisteners\":\"\",\"defaultflow\":\"false\",\"showdiamondmarker\":false},\"stencil\":{\"id\":\"SequenceFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\"}],\"bounds\":{\"lowerRight\":{\"x\":618.9164207579244,\"y\":306.21570089208706},\"upperLeft\":{\"x\":400.5513526795757,\"y\":138.76476785791297}},\"dockers\":[{\"x\":50,\"y\":40},{\"x\":22.166671752929688,\"y\":22}],\"target\":{\"resourceId\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\"}}],\"bounds\":{\"lowerRight\":{\"x\":1200,\"y\":1050},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"ssextensions\":[]}";
    // 生成部署名称和数据 ThinkGem
    JsonNode editorNode = new ObjectMapper().readTree(jsonXml);
    BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(editorNode);

    List<ValidationError> valiErrors = repositoryService.validateProcess(bpmnModel);
    for (ValidationError vError : valiErrors) {
      System.out.println(vError.getActivityId()+"---"+vError.getActivityName()+"---"+vError.getDefaultDescription()+"---"+vError.getProcessDefinitionId()+"---"+vError.getProblem());
    }

    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
    ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
    Deployment deployment = repositoryService.createDeployment().name("大赛流程110-1").addInputStream("process110-1", in).deploy();

    List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
    for (ProcessDefinition processDefinition : list) {
      repositoryService.setProcessDefinitionCategory(processDefinition.getId(), "大赛流程");
    }

    List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
    System.out.println("ProcessDefinitions size = "+processDefinitions.size());
    for (ProcessDefinition pd : processDefinitions) {
      System.out.println(pd.getId()+"---"+pd.getName()+"---"+pd.getCategory());

//      StartFormData form = formService.getStartFormData(pd.getId());
//      if (form != null) {
//        System.out.println(form.getDeploymentId()+"---"+form.getFormKey()+"---"+form.getFormProperties()+"---"+form.getProcessDefinition());
//        List<FormProperty> formPros = form.getFormProperties();
//        for (FormProperty formPro : formPros) {
//          System.out.println(formPro.getId()+"---"+formPro.getName()+"---"+formPro.getValue());
//        }
//      }
    }

    ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();
    System.out.println("-------------------------------------------modelQuery");
    System.out.println(modelQuery.count());
    for (Model mdel : modelQuery.list()) {
      System.out.println(mdel.getId()+"---"+mdel.getKey()+"---"+mdel.getName()+"---"+mdel.getCategory()+"---"+mdel.getVersion());
    }

    ModelQuery modelQueryDeployed = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc().deployed();
    System.out.println("-------------------------------------------modelQueryDeployed");
    System.out.println(modelQueryDeployed.count());
    for (Model mdel : modelQueryDeployed.list()) {
      System.out.println(mdel.getId()+"---"+mdel.getKey()+"---"+mdel.getName()+"---"+mdel.getCategory()+"---"+mdel.getVersion());
    }

    ExecutionQuery executionQuery = runtimeService.createExecutionQuery();
    System.out.println("-------------------------------------------ExecutionQuery");
    System.out.println(executionQuery.count());
    for (Execution execn : executionQuery.list()) {
      System.out.println(execn.getId()+"---"+execn.getActivityId()+"---"+execn.getName()+"---"+execn.getDescription()+"---"+execn.getTenantId());
    }


    TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().orderByTaskCreateTime().desc();
    System.out.println("-------------------------------------------TaskQuery");
    System.out.println(taskQuery.count());
    for (Task task : taskQuery.list()) {
      System.out.println(task.getId()+"---"+task.getOwner()+"---"+task.getParentTaskId()+"---"+task.getName()+"---"+task.getDescription()+"---"+task.getTenantId());
    }

    HistoricDetailQuery historicDetailQuery = historyService.createHistoricDetailQuery();
    System.out.println("-------------------------------------------historicDetailQuery");
    System.out.println(historicDetailQuery.count());
    for (HistoricDetail historicDetail : historicDetailQuery.list()) {
      System.out.println(historicDetail.getId()+"---"+historicDetail.getProcessInstanceId()+"---"+historicDetail.getExecutionId()+"---"+historicDetail.getTaskId()+"---"+historicDetail.getActivityInstanceId());
    }


//    HistoricTaskInstanceQuery histTaskQueryByUser = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
    HistoricTaskInstanceQuery histTaskQueryFinished = historyService.createHistoricTaskInstanceQuery().finished().includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
    System.out.println("-------------------------------------------historicDetailQuery");
    System.out.println(histTaskQueryFinished.count());
    for (HistoricTaskInstance historicTaskInstance : histTaskQueryFinished.list()) {
      System.out.println(historicTaskInstance.getId()+"---"+historicTaskInstance.getProcessInstanceId()+"---"+historicTaskInstance.getExecutionId()+"---"+historicTaskInstance.getName()+"---"+historicTaskInstance.getOwner());
    }

    HistoricTaskInstanceQuery histTaskQueryUnFinished = historyService.createHistoricTaskInstanceQuery().unfinished().includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
    System.out.println("-------------------------------------------histTaskQueryUnFinished");
    System.out.println(histTaskQueryUnFinished.count());
    for (HistoricTaskInstance historicTaskInstance : histTaskQueryUnFinished.list()) {
      System.out.println(historicTaskInstance.getId()+"---"+historicTaskInstance.getProcessInstanceId()+"---"+historicTaskInstance.getExecutionId()+"---"+historicTaskInstance.getName()+"---"+historicTaskInstance.getOwner());
    }
  }
}
