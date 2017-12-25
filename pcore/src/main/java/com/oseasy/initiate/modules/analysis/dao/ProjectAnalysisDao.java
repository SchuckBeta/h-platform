package com.oseasy.initiate.modules.analysis.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface ProjectAnalysisDao {
	public List<Map<String,Object>> getData1();
	public List<Map<String,Object>> getData2();
	public List<Map<String,Object>> getData3();
}
