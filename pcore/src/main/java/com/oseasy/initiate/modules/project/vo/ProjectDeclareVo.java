package com.oseasy.initiate.modules.project.vo;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.entity.ProjectPlan;

/**
 * 项目申报Vo
 * @author 9527
 * @version 2017-03-11
 */
public class ProjectDeclareVo  {
	private ProjectDeclare projectDeclare;
	private List<ProjectPlan> plans;
	private List<Map<String,String>> teamStudent;
	private List<Map<String,String>> teamTeacher;
	private Map<String,String> auditInfo;
	private List<Map<String,String>> fileInfo;
	private Map<String,String> projectAnnounce;
	
	
	public Map<String, String> getProjectAnnounce() {
		return projectAnnounce;
	}

	public void setProjectAnnounce(Map<String, String> projectAnnounce) {
		this.projectAnnounce = projectAnnounce;
	}

	public List<Map<String, String>> getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(List<Map<String, String>> fileInfo) {
		this.fileInfo = fileInfo;
	}

	public Map<String, String> getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(Map<String, String> auditInfo) {
		this.auditInfo = auditInfo;
	}



	public List<Map<String, String>> getTeamStudent() {
		return teamStudent;
	}

	public void setTeamStudent(List<Map<String, String>> teamStudent) {
		this.teamStudent = teamStudent;
	}

	public List<Map<String, String>> getTeamTeacher() {
		return teamTeacher;
	}

	public void setTeamTeacher(List<Map<String, String>> teamTeacher) {
		this.teamTeacher = teamTeacher;
	}

	public ProjectDeclare getProjectDeclare() {
		return projectDeclare;
	}

	public void setProjectDeclare(ProjectDeclare projectDeclare) {
		this.projectDeclare = projectDeclare;
	}

	public List<ProjectPlan> getPlans() {
		return plans;
	}

	public void setPlans(List<ProjectPlan> plans) {
		this.plans = plans;
	}
}