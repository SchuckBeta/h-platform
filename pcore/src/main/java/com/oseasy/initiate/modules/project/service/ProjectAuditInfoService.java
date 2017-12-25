package com.oseasy.initiate.modules.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.project.dao.ProjectDeclareDao;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.project.entity.ProjectAuditInfo;
import com.oseasy.initiate.modules.project.dao.ProjectAuditInfoDao;

/**
 * 项目审核信息Service
 * @author 9527
 * @version 2017-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectAuditInfoService extends CrudService<ProjectAuditInfoDao, ProjectAuditInfo> {
	@Autowired
	ProjectDeclareDao projectDeclareDao;


	public ProjectAuditInfo get(String id) {
		return super.get(id);
	}
	
	public List<ProjectAuditInfo> findList(ProjectAuditInfo projectAuditInfo) {
		return super.findList(projectAuditInfo);
	}
	
	public Page<ProjectAuditInfo> findPage(Page<ProjectAuditInfo> page, ProjectAuditInfo projectAuditInfo) {
		return super.findPage(page, projectAuditInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectAuditInfo projectAuditInfo) {
		super.save(projectAuditInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectAuditInfo projectAuditInfo) {
		super.delete(projectAuditInfo);
	}

	@Transactional(readOnly = false)
	public void deleteByPidAndStep(ProjectAuditInfo projectAuditInfo) {
		dao.deleteByPidAndStep(projectAuditInfo);
	}
	//addBy zhangzheng 获得评审意见
	public List<ProjectAuditInfo> getInfo(ProjectAuditInfo projectAuditInfo) {
		return dao.getInfo(projectAuditInfo);
	}
	//addBy zhangzheng 项目变更 更改审核意见
	@Transactional(readOnly = false)
	public void updateInfoList(List<ProjectAuditInfo> auditInfoList,
							    List<ProjectAuditInfo> midAuditList,
							    List<ProjectAuditInfo> closeAuditList,
							    String projectId,String level,String number,
							    float midScore,float finalScore) {
		//先删除之前的审核记录
		dao.deleteByPid(projectId);
		for(ProjectAuditInfo auditInfo:auditInfoList) {
			auditInfo.setProjectId(projectId);
		}
		for(ProjectAuditInfo auditInfo:midAuditList) {
			auditInfo.setProjectId(projectId);
		}
		for(ProjectAuditInfo auditInfo:closeAuditList) {
			auditInfo.setProjectId(projectId);
		}


		//业务判断
		if (StringUtil.equals("5",auditInfoList.get(0).getGrade())) { //如果是立项不合格 后面的数据没用
			auditInfoList=auditInfoList.subList(0,1);
			//保存评审信息
			auditInfoList.get(0).setId(IdGen.uuid());
			if (auditInfoList.get(0).getCreateDate()==null) {
				auditInfoList.get(0).setCreateDate(new Date());
			}
			dao.insert(auditInfoList.get(0));
			//主表状态改为立项不合格
			ProjectDeclare projectDeclare=new ProjectDeclare();
			projectDeclare.setId(projectId);
			projectDeclare.setFinalResult("3"); //立项不合格
			projectDeclare.setStatus("8");   //项目终止
			//更改项目编号
			String prefix=ProjectDeclare.getPrefix(level);
			number=number.substring(prefix.length(),number.length());
			projectDeclare.setNumber(number);
			projectDeclare.setLevel("");  //更改项目级别
			projectDeclare.setMidScore(0.0f);
			projectDeclare.setFinalScore(0.0f);
			projectDeclareDao.updateFinalResult(projectDeclare);
			return ;
		}

		if (StringUtil.equals("4",auditInfoList.get(0).getGrade())) { //提交是C级，则学校立项审核没用（清空）
			auditInfoList.get(1).setProjectId("");  //projectId为空，数据库不保存
		}


		//如果是中期检查不合格 后面的数据没用
		if (StringUtil.equals("2",auditInfoList.get(2).getGrade())) {
			auditInfoList=auditInfoList.subList(0,3);
			//保存评审信息
			for (ProjectAuditInfo auditInfo:auditInfoList) {
				auditInfo.setId(IdGen.uuid());
				if (auditInfo.getCreateDate()==null) {
					auditInfo.setCreateDate(new Date());
				}
				if (StringUtil.isNotBlank(auditInfo.getProjectId())) {  //projectId为空，数据库不保存
					dao.insert(auditInfo);
				}
			}
			//保存中期评分信息
			for(ProjectAuditInfo auditInfo:midAuditList) {
				auditInfo.setId(IdGen.uuid());
				if (auditInfo.getCreateDate()==null) {
					auditInfo.setCreateDate(new Date());
				}
				dao.insert(auditInfo);
			}

			//主表状态改为中期不合格
			ProjectDeclare projectDeclare=new ProjectDeclare();
			projectDeclare.setId(projectId);
			projectDeclare.setFinalResult("4"); //中期不合格
			projectDeclare.setStatus("8");   //项目终止
			projectDeclare.setMidScore(midScore);
			projectDeclare.setFinalScore(0.0f);
			//更改项目编号
			String prefix=ProjectDeclare.getPrefix(level);
			number=number.substring(prefix.length(),number.length());
			String newLevel=auditInfoList.get(0).getGrade();
			String newPrefix=ProjectDeclare.getPrefix(auditInfoList.get(0).getGrade());
			if (StringUtil.equals(auditInfoList.get(0).getGrade(),"6")) { //提交给学校
				newPrefix=ProjectDeclare.getPrefix(auditInfoList.get(1).getGrade());
				newLevel=auditInfoList.get(1).getGrade();
			}
			number=newPrefix+number;
			projectDeclare.setNumber(number);
			projectDeclare.setLevel(newLevel);  //更改项目级别
			projectDeclareDao.updateFinalResult(projectDeclare);
			return ;
		}
		//如果是中期检查合格
		if (StringUtil.equals("0",auditInfoList.get(2).getGrade())) {
			//保存评审信息
			for (ProjectAuditInfo auditInfo:auditInfoList) {
				auditInfo.setId(IdGen.uuid());
				if (auditInfo.getCreateDate()==null) {
					auditInfo.setCreateDate(new Date());
				}
				if (StringUtil.isNotBlank(auditInfo.getProjectId())) {  //projectId为空，数据库不保存
					dao.insert(auditInfo);
				}
			}
			//保存中期评分信息
			for(ProjectAuditInfo auditInfo:midAuditList) {
				auditInfo.setId(IdGen.uuid());
				if (auditInfo.getCreateDate()==null) {
					auditInfo.setCreateDate(new Date());
				}
				dao.insert(auditInfo);
			}
			//保存结项评分信息
			for(ProjectAuditInfo auditInfo:closeAuditList) {
				auditInfo.setId(IdGen.uuid());
				if (auditInfo.getCreateDate()==null) {
					auditInfo.setCreateDate(new Date());
				}
				dao.insert(auditInfo);
			}

			//主表状态改为中期不合格
			ProjectDeclare projectDeclare=new ProjectDeclare();
			projectDeclare.setId(projectId);
			projectDeclare.setFinalResult(auditInfoList.get(4).getGrade());
			projectDeclare.setStatus("9");   //项目结项
			//更改项目编号
			String prefix=ProjectDeclare.getPrefix(level);
			number=number.substring(prefix.length(),number.length());
			String newLevel=auditInfoList.get(0).getGrade();
			String newPrefix=ProjectDeclare.getPrefix(auditInfoList.get(0).getGrade());
			if (StringUtil.equals(auditInfoList.get(0).getGrade(),"6")) { //提交给学校
				newPrefix=ProjectDeclare.getPrefix(auditInfoList.get(1).getGrade());
				newLevel=auditInfoList.get(1).getGrade();
			}
			number=newPrefix+number;
			projectDeclare.setNumber(number);
			projectDeclare.setLevel(newLevel);  //更改项目级别
			projectDeclare.setMidScore(midScore);
			projectDeclare.setFinalScore(finalScore);
			projectDeclareDao.updateFinalResult(projectDeclare);
			return ;

		}

	}


}