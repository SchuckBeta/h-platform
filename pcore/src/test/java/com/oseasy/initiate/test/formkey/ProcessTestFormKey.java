package com.hch.platform.pcore.test.formkey;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class ProcessTestFormKey extends PluggableActivitiTestCase {

	@Deployment(resources = { "diagrams/form/FormKey.bpmn20.xml", "diagrams/form/start.form", "diagrams/form/first-step.form", "diagrams/form/second-step.form" })
	public void testTaskFormsWithVacationRequestProcess() {

		// Get start form
		String procDefId = repositoryService.createProcessDefinitionQuery().singleResult().getId();
		Object startForm = formService.getRenderedStartForm(procDefId);
		assertNotNull(startForm);
		
		assertEquals("<input id=\"start-name\" />", startForm);
		
		Map<String, String> formProperties = new HashMap<String, String>();
		formProperties.put("startName", "HenryYan");
		ProcessInstance processInstance = formService.submitStartFormData(procDefId, formProperties);
		assertNotNull(processInstance);
		
		Task task = taskService.createTaskQuery().taskAssignee("user1").singleResult();
		Object renderedTaskForm = formService.getRenderedTaskForm(task.getId());
		assertEquals("<input id=\"start-name\" value=\"HenryYan\" />\n<input id=\"first-name\" />", renderedTaskForm);
		
		formProperties = new HashMap<String, String>();
		formProperties.put("firstName", "kafeitu");
		formService.submitTaskFormData(task.getId(), formProperties);
		
		task = taskService.createTaskQuery().taskAssignee("user2").singleResult();
		assertNotNull(task);
		renderedTaskForm = formService.getRenderedTaskForm(task.getId());
		assertEquals("<input id=\"first-name\" value=\"kafeitu\" />", renderedTaskForm);
	}
}