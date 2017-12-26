package com.hch.platform.pcore.modules.cms.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.cms.entity.CmsIndexRegion;

/**
 * 首页区域管理DAO接口
 * @author daichanggeng
 * @version 2017-04-06
 */
@MyBatisDao
public interface CmsIndexRegionDao extends CrudDao<CmsIndexRegion> {
	
}