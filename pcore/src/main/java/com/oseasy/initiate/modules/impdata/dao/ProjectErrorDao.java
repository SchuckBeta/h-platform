package com.oseasy.initiate.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.impdata.entity.ProjectError;

/**
 * 导入项目错误数据表DAO接口
 * @author 9527
 * @version 2017-05-26
 */
@MyBatisDao
public interface ProjectErrorDao extends CrudDao<ProjectError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}