/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.entity.GContestUndergo;
import org.apache.ibatis.annotations.Param;

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
	public void updateAllInfo(Team team);
	public List<Team> findTeamListByUserId(String  userid);
	public Integer hiddenDeleteWithNotify(@Param("tid")String tid,@Param("uid")String uid);
	public Integer checkTeamIsInProject(String tid);
	public Integer checkTeamIsInCyjd(String tid);
	public void auditOne(@Param("teamId")String teamId,@Param("res")String res,@Param("uid")String uid);
	public void auditAllBiuldOver(String uid);
	public void auditAllBiuldIng(String uid);
	public Team findTeamJoinInNums(String teamId);
	public List<Map<String,Object>> checkIsJoinInTUR(@Param("tds")List<Team> tds,@Param("uid")String uid);
	public List<Map<String,Object>> checkIsJoinInTeams(@Param("tds")List<Team> tds,@Param("uid")String uid);
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
	public int findStuNumByTeamId(String teamId);
	public int findTe1NumByTeamId(String teamId);
	public int findTe2NumByTeamId(String teamId);
	public List<Team> selectTeamByName(String name);
	public List<Team> findListByCreatorId(Team team);
	public List<Team> findListByCreatorIdAndState(Team team);	
	int findTeamNumByUserId(TeamUserRelation teamUserRelation);
     //根据两个人的userId，查找所在同一个团队数量
	int findTeamByUserId(@Param("user1Id")String user1Id,@Param("user2Id") String user2Id);

	List<Team> findInTeamList(Team team);

	List<ProjectExpVo> findProjectByTeamId(@Param("teamId")String teamId);

	List<GContestUndergo> findGContestByTeamId(@Param("teamId")String teamId);
}
