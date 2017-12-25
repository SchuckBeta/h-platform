package com.oseasy.initiate.modules.cms.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.cms.entity.CmsIndexRegion;

/**
 * 首页区域管理DAO接口
 * @author daichanggeng
 * @version 2017-04-06
 */
@MyBatisDao
public interface CmsIndexRegionDao extends CrudDao<CmsIndexRegion> {
	
}