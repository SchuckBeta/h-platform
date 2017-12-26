package com.oseasy.initiate.modules.analysis.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.analysis.vo.EchartVo;

@MyBatisDao
public interface GlobalAnalysisDao {
	public List<Map<String,Object>> getData1();
	public List<Map<String,Object>> getData2();
	public List<Map<String,Object>> getData3();
	public int getContestNumber(Map<String, Object> map);
	public int getProjectNumber(Map<String, Object> map);

	List<Map<String,Object>> findAllGcontestType(String year);

	List<Map<String,Object>> findAllProjectType(String year);

	List<Map<String,Object>> findAllGcontestStuCurrState(Map<String, String> year);

	List<Map<String,Object>> findAllTeacherByType(String year);

	List<Map<String,Object>> findAllProjectStuCurrState(Map<String, String> year);

	List<Map<String,Object>> findHotTechnology(Map<String, String>  year);

	List<Map<String,Object>> findTeacherDtn(String year);

	List<String> getProjecTypes();

	List<String> getYears();
}
