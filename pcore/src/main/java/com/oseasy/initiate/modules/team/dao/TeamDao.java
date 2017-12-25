/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamDetails;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

/**
 * 团队信息表DAO接口
 * @author zhangzheng
 * @version 2017-03-06
 */
@MyBatisDao
public interface TeamDao extends CrudDao<Team> {
	public TeamDetails findTeamDetails(String id);
	public List<TeamDetails> findTeamInfo(String id,String usertype);
	/**
	 * 根据团队中间表的teamId修改团队表的团队状态
	 * @param teamUserRelation 
	 */
	public void updateTeamState(Team team);
	/**
	 * 添加团队中间表信息
	 * @param teamUserRelation
	 */
	public void saveTeamUserRelation(TeamUserRelation teamUserRelation);
	/**
	 * 根据teamId查询已加入团队的成员
	 * @param teamId
	 * @return
	 */
	public List<Team> findTeamUserName(String teamId);
/*	public int updateAddState(TeamUserRelation teamUserRelation);*/
	/**
	 * 根据teamId查询团队里面已经存在的组员人数和申请人的类型
	 * @param teamUserRelation
	 * @return
	 */
	public List<Team> findNumByTeamId(TeamUserRelation teamUserRelation);
	/**
	 * 根据teamId查询团队中实际人数
	 * @param teamId
	 * @return
	 */
	public Team findRealityNum(String teamId);

	
	public List<TeamDetails> findTeamByTeamId(String id,String usertype);
	/**
	 * 根据当前登录用户的userid查询已创建的有效的团队
	 * @param curUser
	 * @return
	 */
	public Long countBuildByUserId(User curUser);
	public int findStuNumByTeamId(TeamUserRelation teamUserRelation);
	public int findTe1NumByTeamId(TeamUserRelation teamUserRelation);
	public int findTe2NumByTeamId(TeamUserRelation teamUserRelation);
	public List<Team> selectTeamByName(String name);
	public List<Team> findListByCreatorId(Team team);
	
}
