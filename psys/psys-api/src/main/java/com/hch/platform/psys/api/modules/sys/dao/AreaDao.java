/**
 * 
 */
package com.hch.platform.pcore.modules.sys.dao;

import com.hch.platform.pcore.common.persistence.TreeDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.Area;

/**
 * 区域DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {
	
}
