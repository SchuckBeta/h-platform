/**
 * 
 */
package com.hch.platform.pcore.modules.gen.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.gen.entity.GenTable;

/**
 * 业务表DAO接口


 */
@MyBatisDao
public interface GenTableDao extends CrudDao<GenTable> {
	
}
