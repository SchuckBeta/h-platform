/**
 * 
 */
package com.oseasy.initiate.modules.sys.dao;

import com.oseasy.initiate.common.persistence.TreeDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.Area;

/**
 * 区域DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {
	
}
