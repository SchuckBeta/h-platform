package com.hch.platform.pcore.modules.cms.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.cms.entity.CmsIndexResource;

/**
 * 首页资源DAO接口
 * @author daichanggeng
 * @version 2017-04-07
 */
@MyBatisDao
public interface CmsIndexResourceDao extends CrudDao<CmsIndexResource> {
	
}