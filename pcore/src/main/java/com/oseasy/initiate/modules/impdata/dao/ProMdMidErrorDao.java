package com.oseasy.initiate.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.impdata.entity.ProMdMidError;

/**
 * 民大项目中期检查导入错误数据DAO接口.
 * @author 9527
 * @version 2017-10-18
 */
@MyBatisDao
public interface ProMdMidErrorDao extends CrudDao<ProMdMidError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}