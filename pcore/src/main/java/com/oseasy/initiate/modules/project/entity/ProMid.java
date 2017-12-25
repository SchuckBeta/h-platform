package com.oseasy.initiate.modules.project.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 国创项目中期检查表单Entity
 * @author 9527
 * @version 2017-03-29
 */
public class ProMid extends DataEntity<ProMid> {
	
	private static final long serialVersionUID = 1L;
	private String projectId;		// 项目id
	private String programme;		// 目前存在的问题及解决方案
	private String tutorSuggest;		// 导师建议及意见
	private Date tutorSuggestDate;		// 导师建议及意见填写时间
	private String status;		// 提交状态：1保存，2提交

	private Date taskBeginDate; //任务开始时间
	private Date taskEndDate;   //任务结束时间
	private List<ProSituation> proSituationList;  // 子表  组成员完成情况
	private List<ProProgress> proProgresseList;    // 子表 计划工作任务


	public ProMid() {
		super();
	}

	public ProMid(String id) {
		super(id);
	}


	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	

	public String getProgramme() {
		return programme;
	}

	public void setProgramme(String programme) {
		this.programme = programme;
	}
	

	public String getTutorSuggest() {
		return tutorSuggest;
	}

	public void setTutorSuggest(String tutorSuggest) {
		this.tutorSuggest = tutorSuggest;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getTutorSuggestDate() {
		return tutorSuggestDate;
	}

	public void setTutorSuggestDate(Date tutorSuggestDate) {
		this.tutorSuggestDate = tutorSuggestDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTaskBeginDate() {
		return taskBeginDate;
	}

	public void setTaskBeginDate(Date taskBeginDate) {
		this.taskBeginDate = taskBeginDate;
	}

	public Date getTaskEndDate() {
		return taskEndDate;
	}

	public void setTaskEndDate(Date taskEndDate) {
		this.taskEndDate = taskEndDate;
	}

	public List<ProSituation> getProSituationList() {
		return proSituationList;
	}

	public void setProSituationList(List<ProSituation> proSituationList) {
		this.proSituationList = proSituationList;
	}

	public List<ProProgress> getProProgresseList() {
		return proProgresseList;
	}

	public void setProProgresseList(List<ProProgress> proProgresseList) {
		this.proProgresseList = proProgresseList;
	}
}