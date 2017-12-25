/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.act.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.BaseService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.cmd.CreateAndTakeTransitionCmd;
import com.oseasy.initiate.modules.act.service.cmd.JumpTaskCmd;
import com.oseasy.initiate.modules.act.service.creator.ChainedActivitiesCreator;
import com.oseasy.initiate.modules.act.service.creator.MultiInstanceActivityCreator;
import com.oseasy.initiate.modules.act.service.creator.RuntimeActivityDefinitionEntityIntepreter;
import com.oseasy.initiate.modules.act.service.creator.SimpleRuntimeActivityDefinitionEntity;
import com.oseasy.initiate.modules.act.utils.ActUtils;
import com.oseasy.initiate.modules.act.utils.ProcessDefCache;
import com.oseasy.initiate.modules.act.utils.ProcessDefUtils;
import com.oseasy.initiate.modules.act.vo.ProcessMapVo;
import com.oseasy.initiate.modules.act.vo.ProjectEnd;
import com.oseasy.initiate.modules.actyw.tool.process.rest.ActYwModelRestResource;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 流程定义相关Service


 */
@Service
@Transactional(readOnly = true)
public class ActTaskService extends BaseService {
  protected static final Logger LOGGER = Logger.getLogger(ActTaskService.class);

	@Autowired
	private ActDao actDao;

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




	/**
	 * 2017-2-27  addBy zhangzheng
	 * 获取待审核任务 带分页的
	 * @param page
	 * @param act 查询条件封装
	 * @return
	 */
	public Page<Act> todoListForPage(Page<Act> page, Act act) {
		String userId = UserUtils.getUser().getLoginName();
		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();

		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				todoTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
			}
		}

		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			todoTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		if (act.getBeginDate() != null) {
			todoTaskQuery.taskCreatedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			Date endDate = act.getEndDate();
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(endDate);
			rightNow.add(Calendar.DAY_OF_YEAR,1);//日期加1天
			todoTaskQuery.taskCreatedBefore(rightNow.getTime());
		}
		// =============== 等待签收的任务  ===============
		TaskQuery toClaimQuery = taskService.createTaskQuery().taskCandidateUser(userId)
				.includeProcessVariables().active().orderByTaskCreateTime().desc();

		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			toClaimQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				toClaimQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
			}
		}

		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			toClaimQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		if (act.getBeginDate() != null) {
			toClaimQuery.taskCreatedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			Date endDate = act.getEndDate();
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(endDate);
			rightNow.add(Calendar.DAY_OF_YEAR,1);//日期加1天
			toClaimQuery.taskCreatedBefore(rightNow.getTime());
		}

		// 查询总数
		page.setCount(todoTaskQuery.count()+toClaimQuery.count());
		List<Act> actList = Lists.newArrayList();
		// 查询列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("todo");
			actList.add(e);
		}

		// 查询列表
		List<Task> toClaimList = toClaimQuery.list();
		for (Task task : toClaimList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("claim");
			actList.add(e);
		}
		Collections.sort(actList, new Comparator<Act>() {
			public int compare(Act act1, Act act2) {
				return act2.getTask().getCreateTime().compareTo(act1.getTask().getCreateTime());
			}
		});

		int pageStart=(page.getPageNo()-1)*page.getPageSize();
		int pageEnd=actList.size();
		if (actList.size()>page.getPageNo()*page.getPageSize()) {
			pageEnd=page.getPageNo()*page.getPageSize();
		}
		List<Act> subList=actList.subList(pageStart,pageEnd);
		page.setList(subList);

		return page;
	}


	/**
	 * 获取已审核任务
	 * @param page
	 * @param act 查询条件封装
	 * @return
	 */
	public Page<Act> historicList(Page<Act> page, Act act) {
		String userId = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId());
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}

		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				histTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
			}
		}

		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		if (act.getBeginDate() != null) {
			histTaskQuery.taskCompletedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			histTaskQuery.taskCompletedBefore(act.getEndDate());
		}
		// 查询总数
		page.setCount(histTaskQuery.count());
		// 查询列表
		List<HistoricTaskInstance> histList = histTaskQuery.listPage(page.getFirstResult(), page.getMaxResults());
		//处理分页问题
		List<Act> actList=Lists.newArrayList();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setVars(histTask.getProcessVariables());
//			e.setTaskVars(histTask.getTaskLocalVariables());
//			System.out.println(histTask.getId()+"  =  "+histTask.getProcessVariables() + "  ========== " + histTask.getTaskLocalVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
//			e.setProcIns(runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult());
//			e.setProcExecUrl(ActUtils.getProcExeUrl(task.getProcessDefinitionId()));
			e.setStatus("finish");
			actList.add(e);
			//page.getList().add(e);
		}
		page.setList(actList);
		return page;
	}


	/**
	 * @author zhangzheng 多任务实例查询当前任务环节没完成的数量
	 * @param processDefinitionKey 流程编号
	 * @param taskDefinitionKey 当前用户任务编号
	 * @param proInsId   流程实例id,对应业务表的pro_ins_id字段
	 * @return
	 */
	public Integer taskCount(String processDefinitionKey,String taskDefinitionKey,String proInsId) {
		int count=0;
		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		todoTaskQuery.processDefinitionKey(processDefinitionKey); //流程名称
		todoTaskQuery.taskDefinitionKey(taskDefinitionKey);
		todoTaskQuery.processInstanceId(proInsId);

		count+=todoTaskQuery.count();
		return count;
	}

	/**
	 * @author zhangzheng 多任务实例查询当前任务环节是否完成
	 * @param processDefinitionKey 流程编号
	 * @param taskDefinitionKey 当前用户任务编号
	 * @param proInsId   流程实例id,对应业务表的pro_ins_id字段
	 * @return
	 */
	public boolean isMultiFinished(String processDefinitionKey,String taskDefinitionKey,String proInsId) {
		int count=taskCount(processDefinitionKey,taskDefinitionKey,proInsId);
		if (count==0) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @author zhangzheng 多任务实例查询当前任务环节是否是最后一个
	 * @param processDefinitionKey 流程编号
	 * @param taskDefinitionKey 当前用户任务编号
	 * @param proInsId   流程实例id,对应业务表的pro_ins_id字段
	 * @return
	 */
	public boolean isMultiLast(String processDefinitionKey,String taskDefinitionKey,String proInsId) {
		int count=taskCount(processDefinitionKey,taskDefinitionKey,proInsId);
		if (count==1) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @author zhangzheng 并行多任务实例获取开始后的一个节点的角色名
	 * @param processDefinitionKey  流程编号
	 * @return
	 */
	public String getStartNextRoleName(String processDefinitionKey) {
		String roleName="";
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active();
		processDefinitionQuery.processDefinitionKey(processDefinitionKey);
		List<ProcessDefinition> listProcess= processDefinitionQuery.list();
		String processDefinitionId="";
		for (ProcessDefinition processDefinition:listProcess) {
			if (processDefinitionKey.equals(processDefinition.getKey())) {
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
					roleName=userTask.getAssignee();
				}
			}
		}

		String[] roleNames =roleName.split("\\}");
		String roleName0=roleNames[0];
		String[] realName= roleName0.split("\\{");

		return realName[1];
	}


	/**
	 * @author zhangzheng 并行多任务实例获取下一个节点的角色名
	 * @param taskDefKey 当前节点编号
	 * @param processDefinitionKey 流程编号
	 * @return
	 */
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


	/**
	 * @author zhangzheng 根据流程实例id得到流程定义id
	 * @param proInstId  流程实例Id
	 * @return
     */
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


	/**
	 * 获取待审核列表
	 * @param act 查询条件封装
	 * @return
	 */
	public List<Act> todoList(Act act) {
		String userId = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId());

		List<Act> result = new ArrayList<Act>();
		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();

		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		if (act.getBeginDate() != null) {
			todoTaskQuery.taskCreatedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			todoTaskQuery.taskCreatedBefore(act.getEndDate());
		}

		// 查询列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
//			e.setTaskVars(task.getTaskLocalVariables());
//			System.out.println(task.getId()+"  =  "+task.getProcessVariables() + "  ========== " + task.getTaskLocalVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
//			e.setProcIns(runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult());
//			e.setProcExecUrl(ActUtils.getProcExeUrl(task.getProcessDefinitionId()));
			e.setStatus("todo");
			result.add(e);
		}

		// =============== 等待签收的任务  ===============
		TaskQuery toClaimQuery = taskService.createTaskQuery().taskCandidateUser(userId)
				.includeProcessVariables().active().orderByTaskCreateTime().desc();

		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			toClaimQuery.processDefinitionKey(act.getProcDefKey());
		}
		if (act.getBeginDate() != null) {
			toClaimQuery.taskCreatedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			toClaimQuery.taskCreatedBefore(act.getEndDate());
		}

		// 查询列表
		List<Task> toClaimList = toClaimQuery.list();
		for (Task task : toClaimList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
//			e.setTaskVars(task.getTaskLocalVariables());
//			System.out.println(task.getId()+"  =  "+task.getProcessVariables() + "  ========== " + task.getTaskLocalVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
//			e.setProcIns(runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult());
//			e.setProcExecUrl(ActUtils.getProcExeUrl(task.getProcessDefinitionId()));
			e.setStatus("claim");
			result.add(e);
		}
		return result;
	}



	/**
	 * 获取流转历史列表
	 * @param procInsId 流程实例
	 * @param startAct 开始活动节点名称
	 * @param endAct 结束活动节点名称
	 */
	public List<Act> histoicFlowList(String procInsId, String startAct, String endAct) {
		List<Act> actList = Lists.newArrayList();
		List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId)
				.orderByHistoricActivityInstanceStartTime().asc().orderByHistoricActivityInstanceEndTime().asc().list();

		boolean start = false;
		Map<String, Integer> actMap = Maps.newHashMap();

		for (int i=0; i<list.size(); i++) {

			HistoricActivityInstance histIns = list.get(i);

			// 过滤开始节点前的节点
			if (StringUtil.isNotBlank(startAct) && startAct.equals(histIns.getActivityId())) {
				start = true;
			}
			if (StringUtil.isNotBlank(startAct) && !start) {
				continue;
			}

			// 只显示开始节点和结束节点，并且执行人不为空的任务
			if (StringUtil.isNotBlank(histIns.getAssignee())
					|| "startEvent".equals(histIns.getActivityType())
					|| "endEvent".equals(histIns.getActivityType())) {

				// 给节点增加一个序号
				Integer actNum = actMap.get(histIns.getActivityId());
				if (actNum == null) {
					actMap.put(histIns.getActivityId(), actMap.size());
				}

				Act e = new Act();
				e.setHistIns(histIns);
				// 获取流程发起人名称
				if ("startEvent".equals(histIns.getActivityType())) {
					List<HistoricProcessInstance> il = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).orderByProcessInstanceStartTime().asc().list();
//					List<HistoricIdentityLink> il = historyService.getHistoricIdentityLinksForProcessInstance(procInsId);
					if (il.size() > 0) {
						if (StringUtil.isNotBlank(il.get(0).getStartUserId())) {
							User user = UserUtils.getByLoginName(il.get(0).getStartUserId());
							if (user != null) {
								e.setAssignee(histIns.getAssignee());
								e.setAssigneeName(user.getName());
							}
						}
					}
				}
				// 获取任务执行人名称
				if (StringUtil.isNotEmpty(histIns.getAssignee())) {
					User user = UserUtils.getByLoginName(histIns.getAssignee());
					if (user != null) {
						e.setAssignee(histIns.getAssignee());
						e.setAssigneeName(user.getName());
					}
				}
				// 获取意见评论内容
				if (StringUtil.isNotBlank(histIns.getTaskId())) {
					List<Comment> commentList = taskService.getTaskComments(histIns.getTaskId());
					if (commentList.size()>0) {
						e.setComment(commentList.get(0).getFullMessage());
					}
				}
				actList.add(e);
			}

			// 过滤结束节点后的节点
			if (StringUtil.isNotBlank(endAct) && endAct.equals(histIns.getActivityId())) {
				boolean bl = false;
				Integer actNum = actMap.get(histIns.getActivityId());
				// 该活动节点，后续节点是否在结束节点之前，在后续节点中是否存在
				for (int j=i+1; j<list.size(); j++) {
					HistoricActivityInstance hi = list.get(j);
					Integer actNumA = actMap.get(hi.getActivityId());
					if ((actNumA != null && actNumA < actNum) || StringUtil.equals(hi.getActivityId(), histIns.getActivityId())) {
						bl = true;
					}
				}
				if (!bl) {
					break;
				}
			}
		}
		return actList;
	}

	/**
	 * 获取流程列表
	 * @param category 流程分类
	 */
	public Page<Object[]> processList(Page<Object[]> page, String category) {
		/*
		 * 保存两个对象，一个是ProcessDefinition（流程定义），一个是Deployment（流程部署）
		 */
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active().orderByProcessDefinitionKey().asc();

		if (StringUtil.isNotEmpty(category)) {
			processDefinitionQuery.processDefinitionCategory(category);
		}

		page.setCount(processDefinitionQuery.count());

		List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(page.getFirstResult(), page.getMaxResults());
		for (ProcessDefinition processDefinition : processDefinitionList) {
			String deploymentId = processDefinition.getDeploymentId();
			Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
			page.getList().add(new Object[]{processDefinition, deployment});
		}
		return page;
	}

	/**
	 * 获取流程表单（首先获取任务节点表单KEY，如果没有则取流程开始节点表单KEY）
	 * @return
	 */
	public String getFormKey(String procDefId, String taskDefKey) {
		String formKey = "";
		if (StringUtil.isNotBlank(procDefId)) {
			if (StringUtil.isNotBlank(taskDefKey)) {
				try{
					formKey = formService.getTaskFormKey(procDefId, taskDefKey);
				}catch (Exception e) {
					formKey = "";
				}
			}
			if (StringUtil.isBlank(formKey)) {
				formKey = formService.getStartFormKey(procDefId);
			}
			if (StringUtil.isBlank(formKey)) {
				formKey = "/404";
			}
		}
		logger.debug("getFormKey: {}", formKey);
		return formKey;
	}

	/**
	 * 获取流程实例对象
	 * @param procInsId
	 * @return
	 */
	@Transactional(readOnly = false)
	public ProcessInstance getProcIns(String procInsId) {
		return runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
	}

	/**
	 * 启动流程
	 * @param procDefKey 流程定义KEY
	 * @param businessTable 业务表表名
	 * @param businessId	业务表编号
	 * @return 流程实例ID
	 */
	@Transactional(readOnly = false)
	public String startProcess(String procDefKey, String businessTable, String businessId) {
		return startProcess(procDefKey, businessTable, businessId, "");
	}

	/**
	 * 启动流程
	 * @param procDefKey 流程定义KEY
	 * @param businessTable 业务表表名
	 * @param businessId	业务表编号
	 * @param title			流程标题，显示在待办任务标题
	 * @return 流程实例ID
	 */
	@Transactional(readOnly = false)
	public String startProcess(String procDefKey, String businessTable, String businessId, String title) {
		Map<String, Object> vars = Maps.newHashMap();
		return startProcess(procDefKey, businessTable, businessId, title, vars);
	}

	/**
	 * 启动流程
	 * @param procDefKey 流程定义KEY
	 * @param businessTable 业务表表名
	 * @param businessId	业务表编号
	 * @param title			流程标题，显示在待办任务标题
	 * @param vars			流程变量
	 * @return 流程实例ID
	 */
	@Transactional(readOnly = false)
	public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars) {
		String userId = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId())

		// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
		identityService.setAuthenticatedUserId(userId);

		// 设置流程变量
		if (vars == null) {
			vars = Maps.newHashMap();
		}

		// 设置流程标题
		if (StringUtil.isNotBlank(title)) {
			vars.put("title", title);
		}

		// 启动流程
		ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable+":"+businessId, vars);

		// 更新业务表流程实例ID
		Act act = new Act();
		act.setBusinessTable(businessTable);// 业务表名
		act.setBusinessId(businessId);	// 业务表ID
		act.setProcInsId(procIns.getId());
		actDao.updateProcInsIdByBusinessId(act);
		return act.getProcInsId();
	}

	/**
	 * 获取任务
	 * @param taskId 任务ID
	 */
	public Task getTask(String taskId) {
		return taskService.createTaskQuery().taskId(taskId).singleResult();
	}

	/**
	 * 删除任务
	 * @param taskId 任务ID
	 * @param deleteReason 删除原因
	 */
	@Transactional(readOnly = false)
	public void deleteTask(String taskId, String deleteReason) {
		taskService.deleteTask(taskId, deleteReason);
	}

	/**
	 * 签收任务
	 * @param taskId 任务ID
	 * @param userId 签收用户ID（用户登录名）
	 */
	@Transactional(readOnly = false)
	public void claim(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}


	public void claimByProcInsId(String procInsId,List<String> claims) {
		//下个节点自动签收
		if (claims!=null&&claims.size()==1) {
			String  taskId=getTaskidByProcInsId(procInsId);
			String claimName=claims.get(0);
			taskService.claim(taskId, claimName);
		}
	}

	/**
	 * 提交任务, 并保存意见
	 * @param taskId 任务ID
	 * @param procInsId 流程实例ID，如果为空，则不保存任务提交意见
	 * @param comment 任务提交意见的内容
	 * @param vars 任务变量
	 */
	@Transactional(readOnly = false)
	public void complete(String taskId, String procInsId, String comment, Map<String, Object> vars) {
		complete(taskId, procInsId, comment, "", vars);
	}

	/**
	 * 提交任务, 并保存意见
	 * @param taskId 任务ID
	 * @param procInsId 流程实例ID，如果为空，则不保存任务提交意见
	 * @param comment 任务提交意见的内容
	 * @param title			流程标题，显示在待办任务标题
	 * @param vars 任务变量
	 */
	@Transactional(readOnly = false)
	public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars) {
		// 添加意见
		if (StringUtil.isNotBlank(procInsId) && StringUtil.isNotBlank(comment)) {
			taskService.addComment(taskId, procInsId, comment);
		}

		// 设置流程变量
		if (vars == null) {
			vars = Maps.newHashMap();
		}

		// 设置流程标题
		if (StringUtil.isNotBlank(title)) {
			vars.put("title", title);
		}

		// 提交任务
		taskService.complete(taskId, vars);
	}

	/**
	 * 完成第一个任务
	 * @param procInsId
	 */
	@Transactional(readOnly = false)
	public void completeFirstTask(String procInsId) {
		completeFirstTask(procInsId, null, null, null);
	}

	/**
	 * 完成第一个任务
	 * @param procInsId
	 * @param comment
	 * @param title
	 * @param vars
	 */
	@Transactional(readOnly = false)
	public void completeFirstTask(String procInsId, String comment, String title, Map<String, Object> vars) {
		String userId = UserUtils.getUser().getLoginName();
		Task task = taskService.createTaskQuery().taskAssignee(userId).processInstanceId(procInsId).active().singleResult();
		if (task != null) {
			complete(task.getId(), procInsId, comment, title, vars);
		}
	}


	/**
	 * 添加任务意见
	 */
	public void addTaskComment(String taskId, String procInsId, String comment) {
		taskService.addComment(taskId, procInsId, comment);
	}

	//////////////////  回退、前进、跳转、前加签、后加签、分裂 移植  https://github.com/bluejoe2008/openwebflow  //////////////////////////////////////////////////

	/**
	 * 任务后退一步
	 */
	public void taskBack(String procInsId, Map<String, Object> variables) {
		taskBack(getCurrentTask(procInsId), variables);
	}

	/**
	 * 任务后退至指定活动
	 */
	public void taskBack(TaskEntity currentTaskEntity, Map<String, Object> variables) {
		ActivityImpl activity = (ActivityImpl) ProcessDefUtils
				.getActivity(processEngine, currentTaskEntity.getProcessDefinitionId(), currentTaskEntity.getTaskDefinitionKey())
				.getIncomingTransitions().get(0).getSource();
		jumpTask(currentTaskEntity, activity, variables);
	}

	/**
	 * 任务前进一步
	 */
	public void taskForward(String procInsId, Map<String, Object> variables) {
		taskForward(getCurrentTask(procInsId), variables);
	}

	/**
	 * 任务前进至指定活动
	 */
	public void taskForward(TaskEntity currentTaskEntity, Map<String, Object> variables) {
		ActivityImpl activity = (ActivityImpl) ProcessDefUtils
				.getActivity(processEngine, currentTaskEntity.getProcessDefinitionId(), currentTaskEntity.getTaskDefinitionKey())
				.getOutgoingTransitions().get(0).getDestination();

		jumpTask(currentTaskEntity, activity, variables);
	}

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 */
	public void jumpTask(String procInsId, String targetTaskDefinitionKey, Map<String, Object> variables) {
		jumpTask(getCurrentTask(procInsId), targetTaskDefinitionKey, variables);
	}

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 */
	public void jumpTask(String procInsId, String currentTaskId, String targetTaskDefinitionKey, Map<String, Object> variables) {
		jumpTask(getTaskEntity(currentTaskId), targetTaskDefinitionKey, variables);
	}

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 * @param currentTaskEntity 当前任务节点
	 * @param targetTaskDefinitionKey 目标任务节点（在模型定义里面的节点名称）
	 * @throws Exception
	 */
	public void jumpTask(TaskEntity currentTaskEntity, String targetTaskDefinitionKey, Map<String, Object> variables) {
		ActivityImpl activity = ProcessDefUtils.getActivity(processEngine, currentTaskEntity.getProcessDefinitionId(),
				targetTaskDefinitionKey);
		jumpTask(currentTaskEntity, activity, variables);
	}

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 * @param currentTaskEntity 当前任务节点
	 * @param targetActivity 目标任务节点（在模型定义里面的节点名称）
	 * @throws Exception
	 */
	private void jumpTask(TaskEntity currentTaskEntity, ActivityImpl targetActivity, Map<String, Object> variables) {
		CommandExecutor commandExecutor = ((RuntimeServiceImpl) runtimeService).getCommandExecutor();
		commandExecutor.execute(new JumpTaskCmd(currentTaskEntity, targetActivity, variables));
	}

	/**
	 * 后加签
	 */
	@SuppressWarnings("unchecked")
	public ActivityImpl[] insertTasksAfter(String procDefId, String procInsId, String targetTaskDefinitionKey, Map<String, Object> variables, String... assignees) {
		List<String> assigneeList = new ArrayList<String>();
		assigneeList.add(Authentication.getAuthenticatedUserId());
		assigneeList.addAll(CollectionUtils.arrayToList(assignees));
		String[] newAssignees = assigneeList.toArray(new String[0]);
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(procDefId);
		ActivityImpl prototypeActivity = ProcessDefUtils.getActivity(processEngine, processDefinition.getId(), targetTaskDefinitionKey);
		return cloneAndMakeChain(processDefinition, procInsId, targetTaskDefinitionKey, prototypeActivity.getOutgoingTransitions().get(0).getDestination().getId(), variables, newAssignees);
	}

	/**
	 * 前加签
	 */
	public ActivityImpl[] insertTasksBefore(String procDefId, String procInsId, String targetTaskDefinitionKey, Map<String, Object> variables, String... assignees) {
		ProcessDefinitionEntity procDef = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(procDefId);
		return cloneAndMakeChain(procDef, procInsId, targetTaskDefinitionKey, targetTaskDefinitionKey, variables, assignees);
	}

	/**
	 * 分裂某节点为多实例节点
	 */
	public ActivityImpl splitTask(String procDefId, String procInsId, String targetTaskDefinitionKey, Map<String, Object> variables, String... assignee) {
		return splitTask(procDefId, procInsId, targetTaskDefinitionKey, variables, true, assignee);
	}

	/**
	 * 分裂某节点为多实例节点
	 */
	@SuppressWarnings("unchecked")
	public ActivityImpl splitTask(String procDefId, String procInsId, String targetTaskDefinitionKey, Map<String, Object> variables, boolean isSequential, String... assignees) {
		SimpleRuntimeActivityDefinitionEntity info = new SimpleRuntimeActivityDefinitionEntity();
		info.setProcessDefinitionId(procDefId);
		info.setProcessInstanceId(procInsId);

		RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);

		radei.setPrototypeActivityId(targetTaskDefinitionKey);
		radei.setAssignees(CollectionUtils.arrayToList(assignees));
		radei.setSequential(isSequential);

		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(procDefId);
		ActivityImpl clone = new MultiInstanceActivityCreator().createActivities(processEngine, processDefinition, info)[0];

		TaskEntity currentTaskEntity = this.getCurrentTask(procInsId);

		CommandExecutor commandExecutor = ((RuntimeServiceImpl) runtimeService).getCommandExecutor();
		commandExecutor.execute(new CreateAndTakeTransitionCmd(currentTaskEntity, clone, variables));

//		recordActivitiesCreation(info);
		return clone;
	}

	private TaskEntity getCurrentTask(String procInsId) {
		return (TaskEntity) taskService.createTaskQuery().processInstanceId(procInsId).active().singleResult();
	}

	private TaskEntity getTaskEntity(String taskId) {
		return (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
	}

	@SuppressWarnings("unchecked")
	private ActivityImpl[] cloneAndMakeChain(ProcessDefinitionEntity procDef, String procInsId, String prototypeActivityId, String nextActivityId, Map<String, Object> variables, String... assignees) {
		SimpleRuntimeActivityDefinitionEntity info = new SimpleRuntimeActivityDefinitionEntity();
		info.setProcessDefinitionId(procDef.getId());
		info.setProcessInstanceId(procInsId);

		RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);
		radei.setPrototypeActivityId(prototypeActivityId);
		radei.setAssignees(CollectionUtils.arrayToList(assignees));
		radei.setNextActivityId(nextActivityId);

		ActivityImpl[] activities = new ChainedActivitiesCreator().createActivities(processEngine, procDef, info);

		jumpTask(procInsId, activities[0].getId(), variables);
//		recordActivitiesCreation(info);

		return activities;
	}





	/**
	 * 读取带跟踪的图片
	 * @param executionId	环节ID
	 * @return	封装了各种节点信息
	 */
	public InputStream tracePhoto(String processDefinitionId, String executionId) {
//		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

		List<String> activeActivityIds = Lists.newArrayList();
		if (runtimeService.createExecutionQuery().executionId(executionId).count() > 0) {
			activeActivityIds = runtimeService.getActiveActivityIds(executionId);
		}

		// 不使用spring请使用下面的两行代码
		// ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl)ProcessEngines.getDefaultProcessEngine();
		// Context.setProcessEngineConfiguration(defaultProcessEngine.getProcessEngineConfiguration());

		// 使用spring注入引擎请使用下面的这行代码
		Context.setProcessEngineConfiguration(processEngineFactory.getProcessEngineConfiguration());
//		return ProcessDiagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);
		return processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator()
				.generateDiagram(bpmnModel, "png", activeActivityIds);
	}

	/**
	 * 流程跟踪图信息
	 * @param processInstanceId		流程实例ID
	 * @return	封装了各种节点信息
	 */
	public List<Map<String, Object>> traceProcess(String processInstanceId) throws Exception {
		Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();//执行实例
		Object property = PropertyUtils.getProperty(execution, "activityId");
		String activityId = "";
		if (property != null) {
			activityId = property.toString();
		}
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
		List<ActivityImpl> activitiList = processDefinition.getActivities();//获得当前任务的所有节点

		List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
		for (ActivityImpl activity : activitiList) {

			boolean currentActiviti = false;
			String id = activity.getId();

			// 当前节点
			if (id.equals(activityId)) {
				currentActiviti = true;
			}

			Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity, processInstance, currentActiviti);

			activityInfos.add(activityImageInfo);
		}

		return activityInfos;
	}


	/**
	 * 封装输出信息，包括：当前节点的X、Y坐标、变量信息、任务类型、任务描述
	 * @param activity
	 * @param processInstance
	 * @param currentActiviti
	 * @return
	 */
	private Map<String, Object> packageSingleActivitiInfo(ActivityImpl activity, ProcessInstance processInstance,
														  boolean currentActiviti) throws Exception {
		Map<String, Object> vars = new HashMap<String, Object>();
		Map<String, Object> activityInfo = new HashMap<String, Object>();
		activityInfo.put("currentActiviti", currentActiviti);
		setPosition(activity, activityInfo);
		setWidthAndHeight(activity, activityInfo);

		Map<String, Object> properties = activity.getProperties();
		vars.put("节点名称", properties.get("name"));
		vars.put("任务类型", ActUtils.parseToZhType(properties.get("type").toString()));

		ActivityBehavior activityBehavior = activity.getActivityBehavior();
		logger.debug("activityBehavior={}", activityBehavior);
		if (activityBehavior instanceof UserTaskActivityBehavior) {

			Task currentTask = null;

			// 当前节点的task
			if (currentActiviti) {
				currentTask = getCurrentTaskInfo(processInstance);
			}

			// 当前任务的分配角色
			UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
			TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
			Set<Expression> candidateGroupIdExpressions = taskDefinition.getCandidateGroupIdExpressions();
			if (!candidateGroupIdExpressions.isEmpty()) {

				// 任务的处理角色
				setTaskGroup(vars, candidateGroupIdExpressions);

				// 当前处理人
				if (currentTask != null) {
					setCurrentTaskAssignee(vars, currentTask);
				}
			}
		}

		vars.put("节点说明", properties.get("documentation"));

		String description = activity.getProcessDefinition().getDescription();
		vars.put("描述", description);

		logger.debug("trace variables: {}", vars);
		activityInfo.put("vars", vars);
		return activityInfo;
	}

	/**
	 * 设置任务组
	 * @param vars
	 * @param candidateGroupIdExpressions
	 */
	private void setTaskGroup(Map<String, Object> vars, Set<Expression> candidateGroupIdExpressions) {
		String roles = "";
		for (Expression expression : candidateGroupIdExpressions) {
			String expressionText = expression.getExpressionText();
			String roleName = identityService.createGroupQuery().groupId(expressionText).singleResult().getName();
			roles += roleName;
		}
		vars.put("任务所属角色", roles);
	}

	/**
	 * 设置当前处理人信息
	 * @param vars
	 * @param currentTask
	 */
	private void setCurrentTaskAssignee(Map<String, Object> vars, Task currentTask) {
		String assignee = currentTask.getAssignee();
		if (assignee != null) {
			org.activiti.engine.identity.User assigneeUser = identityService.createUserQuery().userId(assignee).singleResult();
			String userInfo = assigneeUser.getFirstName() + " " + assigneeUser.getLastName();
			vars.put("当前处理人", userInfo);
		}
	}

	/**
	 * 获取当前节点信息
	 * @param processInstance
	 * @return
	 */
	private Task getCurrentTaskInfo(ProcessInstance processInstance) {
		Task currentTask = null;
		try {
			String activitiId = (String) PropertyUtils.getProperty(processInstance, "activityId");
			logger.debug("current activity id: {}", activitiId);

			currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey(activitiId)
					.singleResult();
			logger.debug("current task for processInstance: {}", ToStringBuilder.reflectionToString(currentTask));

		} catch (Exception e) {
			logger.error("can not get property activityId from processInstance: {}", processInstance);
		}
		return currentTask;
	}

	/**
	 * 设置宽度、高度属性
	 * @param activity
	 * @param activityInfo
	 */
	private void setWidthAndHeight(ActivityImpl activity, Map<String, Object> activityInfo) {
		activityInfo.put("width", activity.getWidth());
		activityInfo.put("height", activity.getHeight());
	}

	/**
	 * 设置坐标位置
	 * @param activity
	 * @param activityInfo
	 */
	private void setPosition(ActivityImpl activity, Map<String, Object> activityInfo) {
		activityInfo.put("x", activity.getX());
		activityInfo.put("y", activity.getY());
	}

	public ProcessEngine getProcessEngine() {
		return processEngine;
	}


	/**
	 * 根据数据库表中procInsId得到任务Id
	 * @author zhangzheng
	 * @version 2017-3-15
	 * @param procInsId
	 * @return 任务Id
	 */
	public String getTaskidByProcInsId(String procInsId) {
		List<HistoricTaskInstance> hisList=historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
		if (hisList.size()>0) {
			HistoricTaskInstance hisTaskIns=hisList.get(0);
			return hisTaskIns.getId();
		}else{
			return "";
		}
	}

	/**
	 *根据数据库表中procInsId得到任务名称
	 * @author zhangzheng
	 * @version 2017-3-15
	 * @param procInsId
	 * @return string taskName
	*/
	public String getTaskNameByProcInsId(String procInsId) {
		List<HistoricTaskInstance> hisList=historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
		if (hisList.size()>0) {
			HistoricTaskInstance hisTaskIns=hisList.get(0);
			return hisTaskIns.getName();
		}else{
			return "";
		}
	}



	public String getProcessDefinitionId(String procInsId) {
		List<HistoricTaskInstance> hisList=historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
		if (hisList.size()>0) {
			HistoricTaskInstance hisTaskIns=hisList.get(0);
			return hisTaskIns.getProcessDefinitionId();
		}else{
			return "";
		}
	}

	public boolean isEnd(String processInstanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (processInstance != null) {
			return processInstance.isEnded();
		}
		return false;
	}
}