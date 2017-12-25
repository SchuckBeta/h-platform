package com.oseasy.initiate.modules.project.vo;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.modules.project.entity.weekly.ProjectWeekly;

/**
 * 项目周报Vo
 * @author 9527
 * @version 2017-03-11
 */
public class ProjectWeeklyVo  {
	private ProjectWeekly projectWeekly;
	private ProjectWeekly lastpw;
	private List<Map<String,String>> fileInfo;
	
	

	public ProjectWeekly getProjectWeekly() {
		return projectWeekly;
	}

	public void setProjectWeekly(ProjectWeekly projectWeekly) {
		this.projectWeekly = projectWeekly;
	}

	public ProjectWeekly getLastpw() {
		return lastpw;
	}

	public void setLastpw(ProjectWeekly lastpw) {
		this.lastpw = lastpw;
	}

	public List<Map<String, String>> getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(List<Map<String, String>> fileInfo) {
		this.fileInfo = fileInfo;
	}

}