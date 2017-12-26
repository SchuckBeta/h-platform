package com.hch.platform.pcore.modules.gcontesthots.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.gcontesthots.entity.GcontestHots;

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