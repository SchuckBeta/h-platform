package com.oseasy.initiate.modules.gcontesthots.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.gcontesthots.entity.GcontestHots;

/**
 * 大赛热点DAO接口.
 * @author 9527
 * @version 2017-07-12
 */
@MyBatisDao
public interface GcontestHotsDao extends CrudDao<GcontestHots> {
	public GcontestHots getTop();
	public List<Map<String,Object>> getMore(@Param(value="id")String id,@Param(value="keys") List<String> keys);
	public void updateViews(@Param("param") Map<String,Integer> param);
}