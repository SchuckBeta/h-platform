/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

import java.util.List;

/**
 * 团队人员关系表DAO接口
 * @author zhangzheng
 * @version 2017-03-06
 */
@MyBatisDao
public interface TeamUserRelationDao extends CrudDao<TeamUserRelation> {

    public void updateState(TeamUserRelation teamUserRelation);

    public  TeamUserRelation getByTeamAndUser(TeamUserRelation teamUserRelation);
    public List<TeamUserRelation> getStudents (TeamUserRelation teamUserRelation);
    public List<TeamUserRelation> getTeachers (TeamUserRelation teamUserRelation);

    public List<TeamUserRelation> findUserInfo(TeamUserRelation teamUserRelation); 
    public void updateByUserId(TeamUserRelation teamUserRelation);

    public TeamUserRelation getByUser(User user);
    
    public TeamUserRelation getByTeamUserRelation(TeamUserRelation teamUserRelation);

    public TeamUserRelation findUserById(TeamUserRelation teamUserRelation);
    //不根据team表state=0去查
    public TeamUserRelation findUserById1(TeamUserRelation teamUserRelation);
    
    public void deleteTeamUserInfo(String userId,String teamId);//根据用户id和团队id删除用户信息
    
	public Integer findIsApplyTeam(String teamId,String userId);

    public void updateStateByInfo(TeamUserRelation teamUserRelation);
}