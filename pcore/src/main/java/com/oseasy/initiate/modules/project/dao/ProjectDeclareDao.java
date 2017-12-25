package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.team.entity.Team;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 项目申报DAO接口
 * @author 9527
 * @version 2017-03-11
 */
@MyBatisDao
public interface ProjectDeclareDao extends CrudDao<ProjectDeclare> {

    public List<Team> findTeams(@Param("userid") String userid,@Param("teamid") String teamid);
	public void updateStatus(ProjectDeclare projectDeclare);
	public void updateNumber(ProjectDeclare projectDeclare);
	public void updateMidCount(ProjectDeclare projectDeclare);
	public void updateMidScore(ProjectDeclare projectDeclare);
	public void updateFinalScore(ProjectDeclare projectDeclare);
	public void updateFinalResult(ProjectDeclare projectDeclare);
	public List<Map<String,String>> getMyProjectList(Map<String,Object> param);
	public int getMyProjectListCount(Map<String,Object> param);
	public List<Map<String,String>> getMyProjectListPerson(List<String> ids);
	public List<Map<String,String>> getProjectAuditResult(String projectId);
	public List<Map<String,String>> getProjectAuditInfo(String projectId);
	public ProjectDeclare getVars(String id);  //addby zhangzheng 获得工作流需要查询的数据
	//public List<ProjectExpVo> getExps(String userId); //addby 张正 根据userId得到项目经历
	public List<ProjectExpVo> getExpsByUserId(String userId); //addby 张正 根据userId得到项目经历
	
	public List<Map<String,String>> findTeamStudent(String teamid);
	public List<Map<String,String>> findTeamTeacher(String teamid);
	public List<Map<String,String>> getProjectAnnounceByid(String id);
	public List<Map<String,String>> getValidProjectAnnounce();
	public List<Map<String,String>> getCurProjectInfo(String uid);
	public List<Map<String,String>> getCurProjectInfoByTeam(String tid);
	public List<Map<String,String>> getLastProjectInfo(String uid);
	public int getProjectByName(Map<String,String> param);
	
	public int findByTeamId(String teamId);//根据teamid查询项目是否正在进行中
	public ProjectDeclare getProjectByTeamId(String tid);
	public List<ProjectDeclare> getCurProjectInfoByLeader(String leaderId);
	public List<ProjectDeclare> getProjectByCdn(@Param("num") String num,@Param("name") String name,@Param("uid") String uid);
}