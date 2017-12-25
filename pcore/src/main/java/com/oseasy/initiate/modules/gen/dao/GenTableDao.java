/**
 * 
 */
package com.oseasy.initiate.modules.gen.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.gen.entity.GenTable;

/**
 * 业务表DAO接口


 */
@MyBatisDao
public interface GenTableDao extends CrudDao<GenTable> {
	
}
