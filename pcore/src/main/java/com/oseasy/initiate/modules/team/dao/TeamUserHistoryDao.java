package com.oseasy.initiate.modules.team.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.team.entity.TeamUserHistory;

/**
 * 团队历史纪录DAO接口.
 * @author chenh
 * @version 2017-11-14
 */
@MyBatisDao
public interface TeamUserHistoryDao extends CrudDao<TeamUserHistory> {
	Integer getDoingCountByTeamId(String tid);
	int getBuildingCountByUserId(String uid);
	Integer countByCdn1(@Param("tuhs")List<TeamUserHistory> tuhs,@Param("proid")String proid,@Param("protype")String protype);
	Integer countByCdn2(@Param("tuhs")List<TeamUserHistory> tuhs,@Param("proid")String proid,@Param("protype")String protype,@Param("subtype")String subtype);
	Integer countByCdn3(@Param("tuhs")List<TeamUserHistory> tuhs,@Param("proid")String proid,@Param("protype")String protype,@Param("subtype")String subtype);
	String getProIdByCdn(@Param("uid")String uid,@Param("protype")String protype,@Param("subtype")String subtype,@Param("finish")String finish);
	void insertAll(List<TeamUserHistory> tuhs);
	List<TeamUserHistory> getByProId(@Param("proId")String proId,@Param("teamId")String teamId);
	List<TeamUserHistory> getTeamUserHistoryFromTUR(@Param("tid")String tid,@Param("userType")String userType);
	void deleteByProId(String  proId);
	List<TeamUserHistory> getGcontestInfoByActywId(@Param("id")String id, @Param("actywId")String actywId, @Param("gcontesId")String gcontesId);

	int getWeightTotalByTeamId( @Param("teamId")String teamId, @Param("proId")String proId);

	void updateWeight(TeamUserHistory tur);
	void updateFinishAsClose(String proid);
	void updateDelByProid(String proid);
}