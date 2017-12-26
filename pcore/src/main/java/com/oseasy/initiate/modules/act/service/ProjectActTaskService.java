package com.oseasy.initiate.modules.act.service;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.BaseService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.utils.ProcessDefCache;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.sys.utils.ProjectUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import org.activiti.engine.*;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;

import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

/**
 * 国创项目工作流查询Service
 */
@Service
@Transactional(readOnly = true)
public class ProjectActTaskService extends BaseService {

	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	ActTaskService actTaskService;

	/**
	 * 2017-6-8  addBy zhangzheng
	 * 获取所有的工作流数据（待审核+待签收+已审核）
	 * @param page
	 * @param act 查询条件封装
	 * @return
	 */
	public Page<Act> allListForPage(Page<Act> page,Act act) {
		String userId = UserUtils.getUser().getLoginName();
		// =============== 待审核任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					todoTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			todoTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}
		// =============== 待签收任务  ===============
		TaskQuery toClaimQuery = taskService.createTaskQuery().taskCandidateUser(userId)
				.includeProcessVariables().active().orderByTaskCreateTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			toClaimQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					toClaimQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			toClaimQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		// =============== 已审核任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					histTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		//待审核、待签收、已审核整合
		// 查询总数
		page.setCount(todoTaskQuery.count()+toClaimQuery.count()+histTaskQuery.count());
		page.setTodoCount(todoTaskQuery.count()+toClaimQuery.count());
		List<Act> actList = Lists.newArrayList();  //所有数据
		// 待审核列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("todo");
			actList.add(e);
		}
		// 待签收列表
		List<Task> toClaimList = toClaimQuery.list();
		for (Task task : toClaimList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("claim");
			actList.add(e);
		}
		//已审核列表
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setVars(histTask.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
			String procInsId=histTask.getProcessInstanceId();
			//处理状态
			String projectId= (String) e.getVars().getMap().get("id");
			String stateStr=ProjectUtils.getStatus(projectId);
			if (StringUtil.isNotBlank(stateStr)) {
				e.setTaskName(stateStr);
			}else{
				List<HistoricTaskInstance> hisList2=historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
				if (hisList2.size()>0) {
					HistoricTaskInstance hisTaskIns = hisList2.get(0);
					e.setTaskName("待"+hisTaskIns.getName());
				}
			}
			e.setStatus("finish");
			actList.add(e);
		}

		int pageStart=(page.getPageNo()-1)*page.getPageSize();
		int pageEnd=actList.size();
		if (actList.size()>page.getPageNo()*page.getPageSize()) {
			pageEnd=page.getPageNo()*page.getPageSize();
		}
		List<Act> subList=actList.subList(pageStart,pageEnd);
		page.setList(subList);
		return page;
	}

	//秘书或者管理员中期检查的数据
	public Page<Act> middleRatingList(Page<Act> page,Act act,List<Act> middleScoreList, List<ProjectDeclare> projectList){
		String userId = UserUtils.getUser().getLoginName();
		// =============== 待审核任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					todoTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			todoTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}
		// =============== 待签收任务  ===============
		TaskQuery toClaimQuery = taskService.createTaskQuery().taskCandidateUser(userId)
				.includeProcessVariables().active().orderByTaskCreateTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			toClaimQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					toClaimQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			toClaimQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		// =============== 已审核任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					histTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}


		//待审核、待签收、已审核 另外的项目数据 整合
		// 查询总数
//		page.setCount(todoTaskQuery.count()+toClaimQuery.count()+histTaskQuery.count());
		page.setTodoCount(todoTaskQuery.count()+toClaimQuery.count());
		List<Act> actList = Lists.newArrayList();  //所有数据
		// 待审核列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("todo");
			actList.add(e);
		}
		// 待签收列表
		List<Task> toClaimList = toClaimQuery.list();
		for (Task task : toClaimList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("claim");
			actList.add(e);
		}

		//中期评分数据
		if(middleScoreList!=null){
			for(Act actFinished:middleScoreList){
				actFinished.setStatus("middleScore");
				actList.add(actFinished);
			}
		}


		//另外的项目数据
		if(projectList!=null){
			for(ProjectDeclare pd:projectList){
				Act e = new Act();
				e.setProjectDeclare(pd);
				e.setStatus("other");
				if (StringUtils.isNotBlank(pd.getProcInsId())) {
					String taskName=actTaskService.getTaskNameByProcInsId(pd.getProcInsId());
					e.setTaskName(taskName);
				}

				actList.add(e);
			}
		}


		//已审核列表
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setVars(histTask.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
			String procInsId=histTask.getProcessInstanceId();
			//处理状态
			String projectId= (String) e.getVars().getMap().get("id");
			String stateStr=ProjectUtils.getStatus(projectId);
			if (StringUtil.isNotBlank(stateStr)) {
				e.setTaskName(stateStr);
			}else{
				List<HistoricTaskInstance> hisList2=historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
				if (hisList2.size()>0) {
					HistoricTaskInstance hisTaskIns = hisList2.get(0);
					e.setTaskName("待"+hisTaskIns.getName());
				}
			}
			e.setStatus("finish");
			actList.add(e);
		}

		page.setCount(actList.size());
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
	 * 2017-8-18  addBy zhangzheng
	 * 获取所有的工作流数据（待审核+待签收+已审核） 再加上另外的项目数据
	 * @param page
	 * @param act 查询条件封装
	 * @return
	 */
	public Page<Act> allListForPageAddProjectList(Page<Act> page,Act act,List<ProjectDeclare> projectList) {
		String userId = UserUtils.getUser().getLoginName();
		// =============== 待审核任务  ===============
		TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
				.includeProcessVariables().orderByTaskCreateTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			todoTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					todoTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			todoTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}
		// =============== 待签收任务  ===============
		TaskQuery toClaimQuery = taskService.createTaskQuery().taskCandidateUser(userId)
				.includeProcessVariables().active().orderByTaskCreateTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {
			toClaimQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					toClaimQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			toClaimQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}

		// =============== 已审核任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					histTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}


		//待审核、待签收、已审核 另外的项目数据 整合
		// 查询总数
//		page.setCount(todoTaskQuery.count()+toClaimQuery.count()+histTaskQuery.count());
		page.setTodoCount(todoTaskQuery.count()+toClaimQuery.count());
		List<Act> actList = Lists.newArrayList();  //所有数据
		// 待审核列表
		List<Task> todoList = todoTaskQuery.list();
		for (Task task : todoList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("todo");
			actList.add(e);
		}
		// 待签收列表
		List<Task> toClaimList = toClaimQuery.list();
		for (Task task : toClaimList) {
			Act e = new Act();
			e.setTask(task);
			e.setVars(task.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(task.getProcessDefinitionId()));
			e.setStatus("claim");
			actList.add(e);
		}

		//另外的项目数据
		if(projectList!=null){
			for(ProjectDeclare pd:projectList){
				Act e = new Act();
				e.setProjectDeclare(pd);
				e.setStatus("other");
				if (StringUtils.isNotBlank(pd.getProcInsId())) {
					String taskName=actTaskService.getTaskNameByProcInsId(pd.getProcInsId());
					e.setTaskName(taskName);
				}

				actList.add(e);
			}
		}


		//已审核列表
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setVars(histTask.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
			String procInsId=histTask.getProcessInstanceId();
			//处理状态
			String projectId= (String) e.getVars().getMap().get("id");
			String stateStr=ProjectUtils.getStatus(projectId);
			if (StringUtil.isNotBlank(stateStr)) {
				e.setTaskName(stateStr);
			}else{
				List<HistoricTaskInstance> hisList2=historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
				if (hisList2.size()>0) {
					HistoricTaskInstance hisTaskIns = hisList2.get(0);
					e.setTaskName("待"+hisTaskIns.getName());
				}
			}
			e.setStatus("finish");
			actList.add(e);
		}

		page.setCount(actList.size());
		int pageStart=(page.getPageNo()-1)*page.getPageSize();
		int pageEnd=actList.size();
		if (actList.size()>page.getPageNo()*page.getPageSize()) {
			pageEnd=page.getPageNo()*page.getPageSize();
		}
		List<Act> subList=actList.subList(pageStart,pageEnd);
		page.setList(subList);
		return page;
	}

    //
    public List<Act> finishedList(Act act){
		String userId = UserUtils.getUser().getLoginName();
		// =============== 已审核任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					histTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}
		List<Act> actList = Lists.newArrayList();  //所有数据
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setVars(histTask.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
			e.setStatus("finish");
			String procInsId=histTask.getProcessInstanceId();
			//处理状态
			String projectId= (String) e.getVars().getMap().get("id");
			String stateStr=ProjectUtils.getStatus(projectId);
			if (StringUtil.isNotBlank(stateStr)) {
				e.setTaskName(stateStr);
			}else{
				List<HistoricTaskInstance> hisList2=historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
				if (hisList2.size()>0) {
					HistoricTaskInstance hisTaskIns = hisList2.get(0);
					e.setTaskName("待"+hisTaskIns.getName());
					if (StringUtil.startsWith(hisTaskIns.getTaskDefinitionKey(),act.getStatus())) {
						actList.add(e);
					}
				}
			}

		}
		return actList;
	}

	public Page<Act> finishedListForPage(Page<Act> page,Act act,List<ProjectDeclare> projectList) {
		String userId = UserUtils.getUser().getLoginName();
		// =============== 已审核任务  ===============
		HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
				.includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
		// 设置查询条件
		if (StringUtil.isNotBlank(act.getProcDefKey())) {  //流程名称
			histTaskQuery.processDefinitionKey(act.getProcDefKey());
		}
		//遍历act.getVars().getMap() 设置processVariableValueLike
		if (act.getMap()!=null) {
			for (Map.Entry<String, String> entry : act.getMap().entrySet()) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					histTaskQuery.processVariableValueLike(entry.getKey(),"%"+entry.getValue() +"%");
				}

			}
		}
		if (StringUtil.isNotBlank(act.getTaskDefKey())) {  //addBy zhangzheng 查询阶段
			histTaskQuery.taskDefinitionKeyLike("%"+act.getTaskDefKey()+"%");
		}
		List<Act> actList = Lists.newArrayList();  //所有数据
		List<HistoricTaskInstance> histList = histTaskQuery.list();
		for (HistoricTaskInstance histTask : histList) {
			Act e = new Act();
			e.setHistTask(histTask);
			e.setVars(histTask.getProcessVariables());
			e.setProcDef(ProcessDefCache.get(histTask.getProcessDefinitionId()));
			e.setStatus("finish");
			String procInsId=histTask.getProcessInstanceId();
			//处理状态
			String projectId= (String) e.getVars().getMap().get("id");
			String stateStr=ProjectUtils.getStatus(projectId);
			if (StringUtil.isNotBlank(stateStr)) {
				e.setTaskName(stateStr);
			}else{
				List<HistoricTaskInstance> hisList2=historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().asc().list();
				if (hisList2.size()>0) {
					HistoricTaskInstance hisTaskIns = hisList2.get(0);
					e.setTaskName("待"+hisTaskIns.getName());
					if (StringUtil.startsWith(hisTaskIns.getTaskDefinitionKey(),act.getStatus())) {
						actList.add(e);
					}
				}
			}

		}

		//另外的项目数据
		if(projectList!=null){
			for(ProjectDeclare pd:projectList){
				Act e = new Act();
				e.setProjectDeclare(pd);
				e.setStatus("other");
				if (StringUtils.isNotBlank(pd.getProcInsId())) {
					String taskName=actTaskService.getTaskNameByProcInsId(pd.getProcInsId());
					e.setTaskName(taskName);
				}

				actList.add(e);
			}
		}

		page.setCount(actList.size());
		int pageStart=(page.getPageNo()-1)*page.getPageSize();
		int pageEnd=actList.size();
		if (actList.size()>page.getPageNo()*page.getPageSize()) {
			pageEnd=page.getPageNo()*page.getPageSize();
		}
		List<Act> subList=actList.subList(pageStart,pageEnd);
		page.setList(subList);
		return page;
	}






}
