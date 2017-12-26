/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.hch.platform.pcore.modules.act.service;

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

import com.hch.platform.pcore.modules.team.service.TeamUserHistoryService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.BaseService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.act.dao.ActDao;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.act.service.cmd.CreateAndTakeTransitionCmd;
import com.hch.platform.pcore.modules.act.service.cmd.JumpTaskCmd;
import com.hch.platform.pcore.modules.act.service.creator.ChainedActivitiesCreator;
import com.hch.platform.pcore.modules.act.service.creator.MultiInstanceActivityCreator;
import com.hch.platform.pcore.modules.act.service.creator.RuntimeActivityDefinitionEntityIntepreter;
import com.hch.platform.pcore.modules.act.service.creator.SimpleRuntimeActivityDefinitionEntity;
import com.hch.platform.pcore.modules.act.utils.ActUtils;
import com.hch.platform.pcore.modules.act.utils.ProcessDefCache;
import com.hch.platform.pcore.modules.act.utils.ProcessDefUtils;
import com.hch.platform.pcore.modules.actyw.entity.ActYw;
import com.hch.platform.pcore.modules.actyw.entity.ActYwGnode;
import com.hch.platform.pcore.modules.actyw.entity.ActYwNode;
import com.hch.platform.pcore.modules.actyw.service.ActYwGnodeService;
import com.hch.platform.pcore.modules.actyw.service.ActYwService;
import com.hch.platform.pcore.modules.actyw.tool.process.ActYwTool;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

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
	@Autowired
	private ActYwGnodeService actYwGnodeService;
	@Autowired
	private ActYwService actYwService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private UserService userService;
	@Autowired
	private TeamUserHistoryService teamUserHistoryService;

	/**
	 * 2017-2-27  addBy zhangzheng
	 * 获取待审核任务 带分页的
	 *
	 * @param page
	 * @param act  查询条件封装
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
		if (act.getMap() != null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				todoTaskQuery.processVariableValueLike(entry.getKey(), "%" + entry.getValue() + "%");
			}
		}

		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			todoTaskQuery.taskDefinitionKeyLike("%" + act.getTaskDefKey() + "%");
		}

		if (act.getBeginDate() != null) {
			todoTaskQuery.taskCreatedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			Date endDate = act.getEndDate();
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(endDate);
			rightNow.add(Calendar.DAY_OF_YEAR, 1);//日期加1天
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
		if (act.getMap() != null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				toClaimQuery.processVariableValueLike(entry.getKey(), "%" + entry.getValue() + "%");
			}
		}

		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			toClaimQuery.taskDefinitionKeyLike("%" + act.getTaskDefKey() + "%");
		}

		if (act.getBeginDate() != null) {
			toClaimQuery.taskCreatedAfter(act.getBeginDate());
		}
		if (act.getEndDate() != null) {
			Date endDate = act.getEndDate();
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(endDate);
			rightNow.add(Calendar.DAY_OF_YEAR, 1);//日期加1天
			toClaimQuery.taskCreatedBefore(rightNow.getTime());
		}

		// 查询总数
		page.setCount(todoTaskQuery.count() + toClaimQuery.count());
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
		int pageStart = (page.getPageNo() - 1) * page.getPageSize();
		int pageEnd = actList.size();
		if (actList.size() > page.getPageNo() * page.getPageSize()) {
			pageEnd = page.getPageNo() * page.getPageSize();
		}
		List<Act> subList = actList.subList(pageStart, pageEnd);
		page.setList(subList);

		return page;
	}


	/**
	 * 获取已审核任务
	 *
	 * @param page
	 * @param act  查询条件封装
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
		if (act.getMap() != null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				histTaskQuery.processVariableValueLike(entry.getKey(), "%" + entry.getValue() + "%");
			}
		}

		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%" + act.getTaskDefKey() + "%");
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
		List<Act> actList = Lists.newArrayList();
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
	 * @param processDefinitionKey 流程编号
	 * @param taskDefinitionKey    当前用户任务编号
	 * @param proInsId             流程实例id,对应业务表的pro_ins_id字段
	 * @return
	 * @author zhangzheng 多任务实例查询当前任务环节没完成的数量
	 */
	public Integer taskCount(String processDefinitionKey, String taskDefinitionKey, String proInsId) {
		int count = 0;
		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		todoTaskQuery.processDefinitionKey(processDefinitionKey); //流程名称
		todoTaskQuery.taskDefinitionKey(taskDefinitionKey);
		todoTaskQuery.processInstanceId(proInsId);

		count += todoTaskQuery.count();
		return count;
	}

	/**
	 * @param processDefinitionKey 流程编号
	 * @param taskDefinitionKey    当前用户任务编号
	 * @param proInsId             流程实例id,对应业务表的pro_ins_id字段
	 * @return
	 * @author zhangzheng 多任务实例查询当前任务环节是否完成
	 */
	public boolean isMultiFinished(String processDefinitionKey, String taskDefinitionKey, String proInsId) {
		int count = taskCount(processDefinitionKey, taskDefinitionKey, proInsId);
		if (count == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param processDefinitionKey 流程编号
	 * @param taskDefinitionKey    当前用户任务编号
	 * @param proInsId             流程实例id,对应业务表的pro_ins_id字段
	 * @return
	 * @author zhangzheng 多任务实例查询当前任务环节是否是最后一个
	 */
	public boolean isMultiLast(String processDefinitionKey, String taskDefinitionKey, String proInsId) {
		int count = taskCount(processDefinitionKey, taskDefinitionKey, proInsId);
		if (count == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param processDefinitionKey 流程编号
	 * @return
	 * @author zhangzheng 并行多任务实例获取开始后的一个节点的角色名
	 */
	public String getStartNextRoleName(String processDefinitionKey) {
		String roleName = "";
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active();
		processDefinitionQuery.processDefinitionKey(processDefinitionKey);
		List<ProcessDefinition> listProcess = processDefinitionQuery.list();
		String processDefinitionId = "";
		for (ProcessDefinition processDefinition : listProcess) {
			if (processDefinitionKey.equals(processDefinition.getKey())) {
				processDefinitionId = processDefinition.getId();
			}
		}
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		List<Process> processes = bpmnModel.getProcesses();
		Process process = processes.get(0);
		//获取所有的FlowElement信息
		Collection<FlowElement> flowElements = process.getFlowElements();
		for (FlowElement flowElement : flowElements) {
			//如果是任务节点
			if (flowElement instanceof UserTask) {
				UserTask userTask = (UserTask) flowElement;
				//获取入线信息
				List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
				SequenceFlow incomingFlow = incomingFlows.get(0);
				if (incomingFlow.getSourceRef().contains("start")) {
					roleName = userTask.getAssignee();
				}
			}
		}
		String[] roleNames = roleName.split("\\}");
		String roleName0 = roleNames[0];
		String[] realName = roleName0.split("\\{");
		return realName[1];
	}

	/**
	 * @param processDefinitionKey 流程编号
	 * @return
	 * @author zhangzheng 流程含子流程，并行多任务实例获取开始后的一个节点的角色名
	 */
	public String getProcessStartRoleName(String processDefinitionKey) {
		String roleName = "";
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active();
		processDefinitionQuery.processDefinitionKey(processDefinitionKey);
		List<ProcessDefinition> listProcess = processDefinitionQuery.list();
		String processDefinitionId = "";
		for (ProcessDefinition processDefinition : listProcess) {
			if (processDefinitionKey.equals(processDefinition.getKey())) {
				processDefinitionId = processDefinition.getId();
			}
		}
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		List<Process> processes = bpmnModel.getProcesses();
		Process process = processes.get(0);
		//获取所有的FlowElement信息
		Collection<FlowElement> flowElements = process.getFlowElements();
		for (FlowElement flowElement : flowElements) {
			//查询第一个子流程
			if (flowElement instanceof SubProcess) {
				SubProcess subProcess = (SubProcess) flowElement;
				Collection<FlowElement> incomingFlows = subProcess.getFlowElements();
				for (FlowElement flowElementsub : incomingFlows) {

					if (flowElementsub instanceof UserTask) {
						UserTask userTask = (UserTask) flowElementsub;
						roleName = userTask.getAssignee();
						break;
					}
				}
				if (roleName.length() > 0) {
					break;
				}
			}
		}
		String[] roleNames = roleName.split("\\}");
		String roleName0 = roleNames[0];
		String[] realName = roleName0.split("\\{");
		return realName[1];
	}

	/**
	 * @param taskDefKey           当前节点编号
	 * @param processDefinitionKey 流程编号
	 * @return
	 * @author zhangzheng 含有子流程并行多任务实例获取下一个节点的角色名
	 */
	public String getProcessNextRoleName(String taskDefKey, String processDefinitionKey) {
		String roleName = ""; //${teacher}
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active();
		processDefinitionQuery.processDefinitionKey(processDefinitionKey);
		List<ProcessDefinition> listProcess = processDefinitionQuery.list();
		String processDefinitionId = listProcess.get(0).getId();

		List<UserTask> userTaskList = getSubProcess(processDefinitionId);
		for (int i = 0; i < userTaskList.size(); i++) {
			if (userTaskList.get(i).getId().equals(taskDefKey)) {
				if ((i + 1) == userTaskList.size()) {
					break;
				}
				roleName = userTaskList.get(i + 1).getAssignee();
				break;
			}
		}
		if (StringUtil.isNotBlank(roleName)) {
			String[] roleNames = roleName.split("\\}");
			String roleName0 = roleNames[0];
			String[] realName = roleName0.split("\\{");
			return realName[1];
		} else {
			return "";
		}

	}

	/**
	 * @param taskDefKey           当前节点编号
	 * @param processDefinitionKey 流程编号
	 * @return
	 * @author zhangzheng 含有子流程并行多任务实例获取下一个节点的角色名
	 */
	public String getProcessPreNode(String taskDefKey, String processDefinitionKey) {
		String PreNode = ""; //${teacher}
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active();
		processDefinitionQuery.processDefinitionKey(processDefinitionKey);
		List<ProcessDefinition> listProcess = processDefinitionQuery.list();
		String processDefinitionId = listProcess.get(0).getId();

		List<UserTask> userTaskList = getSubProcess(processDefinitionId);
		for (int i = 0; i < userTaskList.size(); i++) {
			if (userTaskList.get(i).getId().equals(taskDefKey)) {
				if (i == 0) {
					break;
				}
				PreNode = userTaskList.get(i - 1).getId();
				break;
			}
		}
		return PreNode;
	}


	public List<UserTask> getSubProcess(String processDefinitionId) {
		List<UserTask> userTaskList = new ArrayList<UserTask>();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		List<Process> processes = bpmnModel.getProcesses();
		Process process = processes.get(0);
		//获取所有的FlowElement信息
		Collection<FlowElement> flowElements = process.getFlowElements();
		//获取所有任务节点
		for (FlowElement flowElement : flowElements) {
			if (flowElement instanceof SubProcess) {
				SubProcess subProcess = (SubProcess) flowElement;
				Collection<FlowElement> incomingFlows = subProcess.getFlowElements();
				for (FlowElement flowElementsub : incomingFlows) {
					if (flowElementsub instanceof UserTask) {
						UserTask userTask = (UserTask) flowElementsub;
						userTaskList.add(userTask);
					}
				}
			}
		}
		return userTaskList;
	}

	/**
	 * @param taskDefKey           当前节点编号
	 * @param processDefinitionKey 流程编号
	 * @return
	 * @author zhangzheng 并行多任务实例获取下一个节点的角色名
	 */
	public String getNextRoleName(String taskDefKey, String processDefinitionKey) {
		String assignee = ""; //${teacher}
		String nextTaskDefKey = "";
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.latestVersion().active();
		processDefinitionQuery.processDefinitionKey(processDefinitionKey);
		List<ProcessDefinition> listProcess = processDefinitionQuery.list();
		String processDefinitionId = listProcess.get(0).getId();

		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		List<Process> processes = bpmnModel.getProcesses();
		Process process = processes.get(0);
		//获取所有的FlowElement信息
		Collection<FlowElement> flowElements = process.getFlowElements();
		for (FlowElement flowElement : flowElements) {
			//如果是任务节点
			if (flowElement instanceof UserTask) {
				UserTask userTask = (UserTask) flowElement;
				if (taskDefKey.equals(userTask.getId())) { //如果是当前节点，获取下个节点的名称
					List<SequenceFlow> outGoingFlows = userTask.getOutgoingFlows();
					SequenceFlow outGoingFlow = outGoingFlows.get(0);
					if (outGoingFlow.getTargetRef().contains("audit")) {
						nextTaskDefKey = outGoingFlow.getTargetRef();
						break;
					}
				}
			}
		}
		if (StringUtil.isNotBlank(nextTaskDefKey)) {
			for (FlowElement flowElement : flowElements) {
				if (flowElement instanceof UserTask) {
					UserTask userTask = (UserTask) flowElement;
					if (nextTaskDefKey.equals(userTask.getId())) {
						assignee = userTask.getAssignee();
					}
				}
			}

		}

		if (StringUtil.isNotBlank(assignee)) {
			String[] roleNames = assignee.split("\\}");
			String roleName0 = roleNames[0];
			String[] realName = roleName0.split("\\{");
			return realName[1];
		} else {
			return "";
		}

	}


	/**
	 * @param proInstId 流程实例Id
	 * @return
	 * @author zhangzheng 根据流程实例id得到流程定义id
	 */
	public String getProcessDefinitionIdByProInstId(String proInstId) {
		List<HistoricTaskInstance> hisList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(proInstId).list();
		if (hisList.size() > 0) {
			HistoricTaskInstance hisTaskIns = hisList.get(0);
			return hisTaskIns.getProcessDefinitionId();
		} else {
			return "";
		}
	}

	/**
	 * 获取民大待审核列表
	 *
	 * @return
	 */
	public List<String> modelMdtodoList(Act act, String keyName) {
		String userName = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId());
		List<String> result = new ArrayList<String>();
		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userName).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		//对应模块
		todoTaskQuery.taskDefinitionKey(keyName);
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		// 查询列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setVars(task.getProcessVariables());
			result.add((String) e.getVars().getMap().get("id"));
		}
		// =============== 已经审核的任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userName).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		histTaskQuery.taskDefinitionKey(keyName);
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setVars(histTask.getProcessVariables());
			result.add((String) e.getVars().getMap().get("id"));
		}
		//得到民大立项未通过数据的id

		//处理分页问题
		// 查询总数
			/*page.setCount(todoTaskQuery.count()+histTaskQuery.count());
			int pageStart=(page.getPageNo()-1)*page.getPageSize();
			int pageEnd=result.size();
			if (result.size()>page.getPageNo()*page.getPageSize()) {
				pageEnd=page.getPageNo()*page.getPageSize();
			}*/
		/*	List<String> subList=result.subList(pageStart,pageEnd);*/

			/*page.setList(null);*/
		return result;
	}


	/**
	 * 获取待审核列表
	 *
	 * @return
	 */
	public Page<Act> modeltodoList(Page<Act> page, Act act, String keyName) {
		String userName = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId());
		List<Act> result = new ArrayList<Act>();
		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userName).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		//对应模块
		todoTaskQuery.taskDefinitionKeyLike("%" + keyName + "%");
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		// 查询列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("todo");
			result.add(e);
		}
		// =============== 已经审核的任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userName).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		histTaskQuery.taskDefinitionKeyLike("%" + keyName + "%");
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setTaskName(histTask.getName());
			e.setVars(histTask.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
			e.setStatus("finish");
			result.add(e);
		}
		//处理分页问题
		// 查询总数
		page.setCount(todoTaskQuery.count() + histTaskQuery.count());
		int pageStart = (page.getPageNo() - 1) * page.getPageSize();
		int pageEnd = result.size();
		if (result.size() > page.getPageNo() * page.getPageSize()) {
			pageEnd = page.getPageNo() * page.getPageSize();
		}
		List<Act> subList = result.subList(pageStart, pageEnd);

		page.setList(subList);
		return page;
	}

	public String getGnodeName(String gnodeId, String actYwId) {
		String auditGonde = null;
		ActYwGnode actYwGnode = new ActYwGnode();
		actYwGnode.setParent(new ActYwGnode(gnodeId));
		actYwGnode.setGroupId(actYwService.get(actYwId).getGroupId());
		ActYwNode actYwNode = new ActYwNode();
		actYwNode.setLevel("2");
		actYwGnode.setNode(actYwNode);
		//查询当前节点下面任务节点id
		List<ActYwGnode> actYwGnodes = actYwGnodeService.findListByYwGroupAndParent(actYwGnode);
		//查询当前角色 角色id
		List<String> roleIds = UserUtils.getUser().getRoleIdList();

		for (int i = 0; i < actYwGnodes.size(); i++) {
			for (int j = 0; j < roleIds.size(); j++) {
				if (actYwGnodes.get(i).getFlowGroup().equals(roleIds.get(j))) {
					auditGonde = actYwGnodes.get(i).getId();
					break;
				}

			}
			if (StringUtil.isNotEmpty(auditGonde)) {
				break;
			}
		}
		return auditGonde;
	}

	/**
	 * 获取待审核列表
	 *
	 * @return
	 */
	public List<String> getAllTodoId(String actYwId, String gnodeId) {
		String userName = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId());
		List<String> result = new ArrayList<String>();
		gnodeId = getGnodeName(gnodeId, actYwId);

		// =============== 已经签收的任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userName).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		ActYw actYw = actYwService.get(actYwId);
		String key = actYw.getPkey(actYw.getGroup(), actYw.getProProject());
		todoTaskQuery.processDefinitionKey(key);
		//对应模块
		todoTaskQuery.taskDefinitionKeyLike("%" + ActYwTool.FLOW_ID_PREFIX + gnodeId + "%");
		// 查询列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("todo");
			result.add((String) e.getVars().getMap().get("id"));
		}

		return result;
	}

	/**
	 * 获取待审核列表
	 *
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
	 *
	 * @param procInsId 流程实例
	 * @param startAct  开始活动节点名称
	 * @param endAct    结束活动节点名称
	 */
	public List<Act> histoicFlowList(String procInsId, String startAct, String endAct) {
		List<Act> actList = Lists.newArrayList();
		List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId)
				.orderByHistoricActivityInstanceStartTime().asc().orderByHistoricActivityInstanceEndTime().asc().list();

		boolean start = false;
		Map<String, Integer> actMap = Maps.newHashMap();

		for (int i = 0; i < list.size(); i++) {

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
							AbsUser user = UserUtils.getByLoginName(il.get(0).getStartUserId());
							if (user != null) {
								e.setAssignee(histIns.getAssignee());
								e.setAssigneeName(user.getName());
							}
						}
					}
				}
				// 获取任务执行人名称
				if (StringUtil.isNotEmpty(histIns.getAssignee())) {
					AbsUser user = UserUtils.getByLoginName(histIns.getAssignee());
					if (user != null) {
						e.setAssignee(histIns.getAssignee());
						e.setAssigneeName(user.getName());
					}
				}
				// 获取意见评论内容
				if (StringUtil.isNotBlank(histIns.getTaskId())) {
					List<Comment> commentList = taskService.getTaskComments(histIns.getTaskId());
					if (commentList.size() > 0) {
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
				for (int j = i + 1; j < list.size(); j++) {
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
	 *
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
	 *
	 * @return
	 */
	public String getFormKey(String procDefId, String taskDefKey) {
		String formKey = "";
		if (StringUtil.isNotBlank(procDefId)) {
			if (StringUtil.isNotBlank(taskDefKey)) {
				try {
					formKey = formService.getTaskFormKey(procDefId, taskDefKey);
				} catch (Exception e) {
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
	 *
	 * @param procInsId
	 * @return
	 */
	@Transactional(readOnly = false)
	public ProcessInstance getProcIns(String procInsId) {
		return runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
	}

	/**
	 * 启动流程
	 *
	 * @param procDefKey    流程定义KEY
	 * @param businessTable 业务表表名
	 * @param businessId    业务表编号
	 * @return 流程实例ID
	 */
	@Transactional(readOnly = false)
	public String startProcess(String procDefKey, String businessTable, String businessId) {
		return startProcess(procDefKey, businessTable, businessId, "");
	}


	/**
	 * 挂起流程
	 *
	 * @param processInstanceId 流程实例id
	 */
	@Transactional(readOnly = false)
	public void suspendProcess(String processInstanceId) {

		runtimeService.suspendProcessInstanceById(processInstanceId);
		ProcessInstance pro = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
	}


	/**
	 * 走到流程下一步
	 *
	 * @param proModel
	 */
	@Transactional(readOnly = false)
	public void runNextProcess(ProModel proModel) {
		ActYw actYw = actYwService.get(proModel.getActYwId());

		Map<String, Object> vars = new HashMap<String, Object>();
		String key = actYw.getPkey(actYw.getGroup(), actYw.getProProject());

		String taskId = getTaskidByProcInsId(proModel.getProcInsId());
		String taskDefinitionKeyaskDefKey = getTask(taskId).getTaskDefinitionKey();
		//String nodeRoleId=getProcessStartRoleName(actYw.getPkey(actYw.getGroup(),actYw.getProProject()));  //从工作流中查询 下一步的角色集合
		/*
		List<String> roles=new ArrayList<String>();
		String nextRoleId=getProcessNextRoleName(taskDefinitionKeyaskDefKey,key);
		String roleId=nextRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());
		Role role= systemService.getNamebyId(roleId);
		if(role!=null){
			//启动节点
			String roleName=role.getName();
			if(roleName.contains("院")||roleName.contains("秘书")){
				roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
			}else{
				roles=userService.getRolesByName(role.getEnname());
			}
			if(roleId.equals(SysIds.SYS_ROLE_USER.getId())){
				roles.clear();
				roles.add(userService.findUserById(proModel.getDeclareId()).getName());
			}
			vars=proModel.getVars();
			vars.put(nextRoleId+"s",roles);
		}*/

		String nextGnodeRoleId = getProcessNextRoleName(taskDefinitionKeyaskDefKey, key);
		if (StringUtil.isNotEmpty(nextGnodeRoleId)) {
			String nextRoleId = nextGnodeRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());
			Role role = systemService.getNamebyId(nextRoleId);
			//启动节点
			String roleName = role.getName();
			List<String> roles = new ArrayList<String>();
			if (roleName.contains(SysIds.ISCOLLEGE.getRemark()) || roleName.contains(SysIds.ISMS.getRemark())) {
				roles = userService.getRolesByName(role.getEnname(), proModel.getDeclareId());
			} else {
				roles = userService.getRolesByName(role.getEnname());
			}
			//后台学生角色id

			if (nextRoleId.equals(SysIds.SYS_ROLE_USER.getId())) {
				roles.clear();
				roles.add(userService.findUserById(proModel.getDeclareId()).getName());
			}
			vars = proModel.getVars();
			//List<String> roles=userService.getCollegeExperts(proModel.getDeclareId());
			vars.put(nextGnodeRoleId + "s", roles);
		} else {
			//更改完成后团队历史表中的状态
			teamUserHistoryService.updateFinishAsClose(proModel.getId());
			//流程没有角色为没有后续流程 将流程表示为已经结束
			proModel.setState("1");
		}

		if (taskId != null) {
			taskService.complete(taskId, vars);
			ProcessInstance pro = runtimeService.createProcessInstanceQuery().processInstanceId(proModel.getProcInsId()).singleResult();
		}
	}


	/**
	 * 启动流程
	 *
	 * @param procDefKey    流程定义KEY
	 * @param businessTable 业务表表名
	 * @param businessId    业务表编号
	 * @param title         流程标题，显示在待办任务标题
	 * @return 流程实例ID
	 */
	@Transactional(readOnly = false)
	public String startProcess(String procDefKey, String businessTable, String businessId, String title) {
		Map<String, Object> vars = Maps.newHashMap();
		return startProcess(procDefKey, businessTable, businessId, title, vars);
	}

	/**
	 * 启动流程
	 *
	 * @param procDefKey    流程定义KEY
	 * @param businessTable 业务表表名
	 * @param businessId    业务表编号
	 * @param title         流程标题，显示在待办任务标题
	 * @param vars          流程变量
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
		ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable + ":" + businessId, vars);
		// 更新业务表流程实例ID
		Act act = new Act();
		act.setBusinessTable(businessTable);// 业务表名
		act.setBusinessId(businessId);    // 业务表ID
		act.setProcInsId(procIns.getId());
		actDao.updateProcInsIdByBusinessId(act);
		return act.getProcInsId();
	}

	/**
	 * 获取任务
	 *
	 * @param taskId 任务ID
	 */
	public Task getTask(String taskId) {
		return taskService.createTaskQuery().taskId(taskId).singleResult();
	}

	/**
	 * 删除任务
	 *
	 * @param taskId       任务ID
	 * @param deleteReason 删除原因
	 */
	@Transactional(readOnly = false)
	public void deleteTask(String taskId, String deleteReason) {
		taskService.deleteTask(taskId, deleteReason);
	}

	/**
	 * 签收任务
	 *
	 * @param taskId 任务ID
	 * @param userId 签收用户ID（用户登录名）
	 */
	@Transactional(readOnly = false)
	public void claim(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}


	public void claimByProcInsId(String procInsId, List<String> claims) {
		//下个节点自动签收
		if (claims != null && claims.size() == 1) {
			String taskId = getTaskidByProcInsId(procInsId);
			String claimName = claims.get(0);
			taskService.claim(taskId, claimName);
		}
	}

	/**
	 * 提交任务, 并保存意见
	 *
	 * @param taskId    任务ID
	 * @param procInsId 流程实例ID，如果为空，则不保存任务提交意见
	 * @param comment   任务提交意见的内容
	 * @param vars      任务变量
	 */
	@Transactional(readOnly = false)
	public void complete(String taskId, String procInsId, String comment, Map<String, Object> vars) {
		complete(taskId, procInsId, comment, "", vars);
	}

	/**
	 * 提交任务, 并保存意见
	 *
	 * @param taskId    任务ID
	 * @param procInsId 流程实例ID，如果为空，则不保存任务提交意见
	 * @param comment   任务提交意见的内容
	 * @param title     流程标题，显示在待办任务标题
	 * @param vars      任务变量
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
	 *
	 * @param procInsId
	 */
	@Transactional(readOnly = false)
	public void completeFirstTask(String procInsId) {
		completeFirstTask(procInsId, null, null, null);
	}

	/**
	 * 完成第一个任务
	 *
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
	 *
	 * @param currentTaskEntity       当前任务节点
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
	 *
	 * @param currentTaskEntity 当前任务节点
	 * @param targetActivity    目标任务节点（在模型定义里面的节点名称）
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
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(procDefId);
		ActivityImpl prototypeActivity = ProcessDefUtils.getActivity(processEngine, processDefinition.getId(), targetTaskDefinitionKey);
		return cloneAndMakeChain(processDefinition, procInsId, targetTaskDefinitionKey, prototypeActivity.getOutgoingTransitions().get(0).getDestination().getId(), variables, newAssignees);
	}

	/**
	 * 前加签
	 */
	public ActivityImpl[] insertTasksBefore(String procDefId, String procInsId, String targetTaskDefinitionKey, Map<String, Object> variables, String... assignees) {
		ProcessDefinitionEntity procDef = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(procDefId);
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

		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(procDefId);
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
	 *
	 * @param executionId 环节ID
	 * @return 封装了各种节点信息
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
	 *
	 * @param processInstanceId 流程实例ID
	 * @return 封装了各种节点信息
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
	 *
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
	 *
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
	 *
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
	 *
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
	 *
	 * @param activity
	 * @param activityInfo
	 */
	private void setWidthAndHeight(ActivityImpl activity, Map<String, Object> activityInfo) {
		activityInfo.put("width", activity.getWidth());
		activityInfo.put("height", activity.getHeight());
	}

	/**
	 * 设置坐标位置
	 *
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
	 *
	 * @param procInsId
	 * @return 任务Id
	 * @author zhangzheng
	 * @version 2017-3-15
	 */
	public String getTaskidByProcInsId(String procInsId) {
		List<HistoricTaskInstance> hisList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
		if (hisList.size() > 0) {
			HistoricTaskInstance hisTaskIns = hisList.get(0);
			return hisTaskIns.getId();
		} else {
			return "";
		}
	}

	/**
	 * 根据数据库表中procInsId得到任务名称
	 *
	 * @param procInsId
	 * @return string taskName
	 * @author zhangzheng
	 * @version 2017-3-15
	 */
	public String getTaskNameByProcInsId(String procInsId) {
		List<HistoricTaskInstance> hisList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
		if (hisList.size() > 0) {
			HistoricTaskInstance hisTaskIns = hisList.get(0);
			return hisTaskIns.getName();
		} else {
			return "";
		}
	}


	public String getProcessDefinitionId(String procInsId) {
		List<HistoricTaskInstance> hisList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
		if (hisList.size() > 0) {
			HistoricTaskInstance hisTaskIns = hisList.get(0);
			return hisTaskIns.getProcessDefinitionId();
		} else {
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

	/**
	 * 查询流程当前节点的下一步节点。用于流程提示时的提示。
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	/*public Map<String, FlowNode> findNextTask(String taskId) throws Exception{
	Map<String, org.activiti.bpmn.model.FlowNode> nodeMap = new HashMap<String, org.activiti.bpmn.model.FlowNode>();
	ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
	//查询当前节点
	HistoricTaskInstance histask =
			findHistricTaskById(taskId, processInstance.getProcessInstanceId());
	//查询流程定义 。
	BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
	List<org.activiti.bpmn.model.Process> listp = bpmnModel.getProcesses();
	org.activiti.bpmn.model.Process process = listp.get(0);
	//当前节点流定义
	FlowNode sourceFlowElement = ( FlowNode) process.getFlowElement(histask.getTaskDefinitionKey());
	// 找到当前任务的流程变量
	List<HistoricVariableInstance> listVar=historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list() ;
	iteratorNextNodes(process, sourceFlowElement, nodeMap,listVar);
	return nodeMap;
	}

*/

	/**
	 * 查询流程当前节点的下一步节点。用于流程提示时的提示。
	 *
	 * @param process
	 * @param sourceFlowElement
	 * @param nodeMap
	 * @param listVar
	 * @throws Exception
	 */
	private void iteratorNextNodes(org.activiti.bpmn.model.Process process, FlowNode sourceFlowElement, Map<String, FlowNode> nodeMap, List<HistoricVariableInstance> listVar)
			throws Exception {
		List<SequenceFlow> list = sourceFlowElement.getOutgoingFlows();
		for (SequenceFlow sf : list) {
			sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
			if (StringUtils.isNotEmpty(sf.getConditionExpression())) {
				ExpressionFactory factory = new ExpressionFactoryImpl();
				SimpleContext context = new SimpleContext();
				for (HistoricVariableInstance var : listVar) {
					context.setVariable(var.getVariableName(), factory.createValueExpression(var.getValue(), var.getValue().getClass()));
				}
				ValueExpression e = factory.createValueExpression(context, sf.getConditionExpression(), boolean.class);
				if ((Boolean) e.getValue(context)) {
					nodeMap.put(sourceFlowElement.getId(), sourceFlowElement);
					break;
				}
			}
			if (sourceFlowElement instanceof org.activiti.bpmn.model.UserTask) {
				nodeMap.put(sourceFlowElement.getId(), sourceFlowElement);
				break;
			} else if (sourceFlowElement instanceof org.activiti.bpmn.model.ExclusiveGateway) {
				iteratorNextNodes(process, sourceFlowElement, nodeMap, listVar);
			}
		}
	}

	/**
	 * 根据实例编号查找下一个任务节点
	 *
	 * @param procInstId ：实例编号
	 * @return
	 */
	public TaskDefinition nextTaskDefinition(String procInstId) {
		//流程标示
		String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult().getProcessDefinitionId();

		ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
		//执行实例
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
		//当前实例的执行到哪个节点
		String activitiId = execution.getActivityId();
		//获得当前任务的所有节点
		List<ActivityImpl> activitiList = def.getActivities();
		String id = null;
		for (ActivityImpl activityImpl : activitiList) {
			id = activityImpl.getId();
			if (activitiId.equals(id)) {
				System.out.println("当前任务：" + activityImpl.getProperty("name"));
				return nextTaskDefinition(activityImpl, activityImpl.getId(), "${iscorrect==1}");
				//              System.out.println(taskDefinition.getCandidateGroupIdExpressions().toArray()[0]);
				//              return taskDefinition;
			}
		}
		return null;
	}

	/**
	 * 下一个任务节点
	 *
	 * @param activityImpl
	 * @param activityId
	 * @param elString
	 * @return
	 */
	private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString) {
		if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
			TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
			//              taskDefinition.getCandidateGroupIdExpressions().toArray();
			return taskDefinition;
		} else {
			List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
			List<PvmTransition> outTransitionsTemp = null;
			for (PvmTransition tr : outTransitions) {
				PvmActivity ac = tr.getDestination(); //获取线路的终点节点
				if ("exclusiveGateway".equals(ac.getProperty("type"))) {
					outTransitionsTemp = ac.getOutgoingTransitions();
					if (outTransitionsTemp.size() == 1) {
						return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, elString);
					} else if (outTransitionsTemp.size() > 1) {
						for (PvmTransition tr1 : outTransitionsTemp) {
							Object s = tr1.getProperty("conditionText");
							if (elString.equals(StringUtils.trim(s.toString()))) {
								return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString);
							}
						}
					}
				} else if ("userTask".equals(ac.getProperty("type"))) {
					return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
				} else {

				}
			}
			return null;
		}
	}

	public boolean getGrateBygondeId(String gondeId) {
		boolean res = false;
		ActYwGnode actYwGnode = actYwGnodeService.get(gondeId);
		List<ActYwGnode> actYwGnodeList = actYwGnodeService.findByYwParentIdsLike(actYwGnode);
		for (ActYwGnode actYwGnodeIndex : actYwGnodeList) {
			if (actYwGnodeIndex.getForm() != null) {
				String path = actYwGnodeIndex.getForm().getPath();
				//判断是否为立项
				if (path.contains("grate")) {
					res = true;
					break;
				}
			}
		}
		return res;
	}

	public ActYwGnode getFrontByGondeId(String gondeId) {
		ActYwGnode res = null;
		ActYwGnode actYwGnode = new ActYwGnode();
		actYwGnode.setParent(new ActYwGnode(gondeId));
		//actYwGnodeService.get(gondeId);
		List<ActYwGnode> actYwGnodeList = actYwGnodeService.findByYwParentIdsLike(actYwGnode);
		for (ActYwGnode actYwGnodeIndex : actYwGnodeList) {
			if (actYwGnodeIndex.getForm() != null) {
				String clientType = actYwGnodeIndex.getForm().getClientType();
				//判断表单类型
				if (clientType.contains("1")) {
					res = actYwGnodeIndex;
					break;
				}
			}
		}
		return res;
	}


	public ActYwGnode getNodeByProInsId(String proInsId) {
		return getNodeByProInsIdByGroupId(null, proInsId);
	}

	public ActYwGnode getNodeByProInsIdByGroupId(String groupId, String proInsId) {
		ProcessInstance pro = runtimeService.createProcessInstanceQuery().processInstanceId(proInsId).singleResult();
		if (StringUtil.isEmpty(proInsId) && StringUtil.isNotEmpty(groupId)) {
			return actYwGnodeService.getStart(groupId);
		}
		if ((pro == null) && StringUtil.isNotEmpty(groupId)) {
			return actYwGnodeService.getEnd(groupId);
		}
		if ((pro == null)) {
			return null;
		}
		String procDefId = getProcessDefinitionIdByProInstId(proInsId);
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(procDefId).singleResult();
		if (processDefinition == null) {
			return null;
		}
		ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl) processDefinition;
		String processDefinitionId = pdImpl.getId();// 流程标识
		ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processDefinitionId);
		List<ActivityImpl> activitiList = def.getActivities();// 获得当前任务的所有节点
		//activitiList.get(0).getId();

		List<String> activeActivityIds = runtimeService.getActiveActivityIds(proInsId);
		try {
			activeActivityIds = runtimeService.getActiveActivityIds(proInsId);
		} catch (ActivitiObjectNotFoundException e) {
			activeActivityIds = Lists.newArrayList();
		}
		String gnodeId = null;
		for (String activeId : activeActivityIds) {
			for (ActivityImpl activityImpl : activitiList) {
				String id = activityImpl.getId();
				if (activityImpl.isScope()) {
					if (activityImpl.getActivities().size() > 1) {
						List<ActivityImpl> subAcList = activityImpl.getActivities();
						for (ActivityImpl subActImpl : subAcList) {
							String subid = subActImpl.getId();
							if (activeId.equals(subid)) {// 获得执行到那个节点
								gnodeId = subid;
								break;
							}
						}
					}
				}
				if (activeId.equals(id)) {
					// 获得执行到那个节点
					gnodeId = id;
					break;
				}
			}
			if (StringUtil.isNotEmpty(gnodeId)) {
				break;
			}
		}
		ActYwGnode actYwGnode = new ActYwGnode();
		if (StringUtil.isNotEmpty(gnodeId)) {
			if (gnodeId.contains(ActYwTool.FLOW_ID_PREFIX)) {
				actYwGnode = actYwGnodeService.get(gnodeId.substring(ActYwTool.FLOW_ID_PREFIX.length()));
			} else if (gnodeId.contains(ActYwTool.FLOW_ID_START)) {
				actYwGnode = actYwGnodeService.get(gnodeId.substring(ActYwTool.FLOW_ID_START.length()));
			}
		}
		actYwGnode.setSuspended(pro.isSuspended());
		return actYwGnode;
	}

}