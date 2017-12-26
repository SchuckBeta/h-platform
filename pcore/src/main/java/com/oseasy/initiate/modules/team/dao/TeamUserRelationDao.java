/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.team.entity.TeamUserHistory;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

import java.util.List;

/**
 * 团队人员关系表DAO接口
 * @author zhangzheng
 * @version 2017-03-06
 */
@MyBatisDao
public interface TeamUserRelationDao extends CrudDao<TeamUserRelation> {
	public void deleteByTeamId(String tid);
	public void insertAll(List<TeamUserHistory> tuhs);
	public List<TeamUserRelation> getByTeamId (String teamId);
    public TeamUserRelation findUserWillJoinTeam(TeamUserRelation teamUserRelation);
    public TeamUserRelation findUserHasJoinTeam(TeamUserRelation teamUserRelation);
	public void hiddenDelete(TeamUserRelation teamUserRelation);
    public void updateStateInTeam(TeamUserRelation teamUserRelation);

    public  TeamUserRelation getByTeamAndUser(TeamUserRelation teamUserRelation);
    public List<TeamUserRelation> getStudents (TeamUserRelation teamUserRelation);
    public List<TeamUserRelation> getTeachers (TeamUserRelation teamUserRelation);

    public TeamUserRelation findUserInfo(TeamUserRelation teamUserRelation); 
    public void updateByUserId(TeamUserRelation teamUserRelation);

    public TeamUserRelation getByUser(User user);
    
    public TeamUserRelation getByTeamUserRelation(TeamUserRelation teamUserRelation);

    public TeamUserRelation findUserById(TeamUserRelation teamUserRelation);

    
    public void deleteTeamUserInfo(String userId,String teamId);//根据用户id和团队id删除用户信息
    
	public Integer findIsApplyTeam(String teamId,String userId);


    public  void updateWeight(TeamUserRelation teamUserRelation);

    public int getWeightTotalByTeamId(String teamId);

    String getTeamStudentName(String teamId);

    String getTeamTeacherName(String teamId);
}