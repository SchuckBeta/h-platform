package com.oseasy.initiate.modules.cms.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.cms.entity.CmsIndexResource;

/**
 * 首页资源DAO接口
 * @author daichanggeng
 * @version 2017-04-07
 */
@MyBatisDao
public interface CmsIndexResourceDao extends CrudDao<CmsIndexResource> {
	
}