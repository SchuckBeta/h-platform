package com.oseasy.initiate.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.impdata.entity.GcontestError;

/**
 * 导入互联网+大赛错误数据DAO接口.
 * @author 奔波儿灞
 * @version 2017-12-07
 */
@MyBatisDao
public interface GcontestErrorDao extends CrudDao<GcontestError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}