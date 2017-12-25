package com.oseasy.initiate.modules.analysis.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface GlobalAnalysisDao {
	public List<Map<String,Object>> getData1();
	public List<Map<String,Object>> getData2();
	public List<Map<String,Object>> getData3();
	public int getContestNumber(Map<String, Object> map);
	public int getProjectNumber(Map<String, Object> map);
}
