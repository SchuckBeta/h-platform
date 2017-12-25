package com.oseasy.initiate.modules.gcontest.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.gcontest.entity.GAuditInfo;
import com.oseasy.initiate.modules.gcontest.entity.GContest;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 大赛信息DAO接口
 * @author zy
 * @version 2017-03-11
 */
@MyBatisDao
public interface GContestDao extends CrudDao<GContest> {
    public void updateState(GContest gContest);

	public int getMyGcontestListCount(Map<String, Object> param);

	public List<Map<String, String>> getMyGcontestList(Map<String, Object> param);

	public List<Map<String, String>> getMyGcontestListPerson(List<String> ids);
	
	public List<Map<String, String>> getGcontestList(Map<String, Object> param);
	
	public int getGcontestListCount(Map<String, Object> param);
	
	public List<Map<String, String>> getGcontestListPerson(List<String> ids);

	public List<GContest> getGcontestByName(String gContestName);
	
	public List<GContest> getGcontestInfo(String gcontestUserId);

	public GContest getLastGcontestInfo(String gcontestUserId);

	public List<Map<String, String>> getEndGcontestList(Map<String, Object> param);

	public int getEndGcontestListCount(Map<String, Object> param);

	public List<GContest> getGcontestByNameNoId(@Param("id") String id, @Param("pName")String pName);

	public int getGcontestChangeListCount(Map<String, Object> param);

	public List<Map<String, String>> getGcontestChangeList(Map<String, Object> param);

	int findGcontestByTeamId(String id);
	
	List<Map<String,String>> getAuditList(Map<String, Object> param);

	int getAuditListCount(Map<String, Object> param);

	int getTodoCount(Map<String, Object> param);

	int getHasdoCount(Map<String, Object> param);
}

