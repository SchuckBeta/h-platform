/**
 * 
 */
package com.oseasy.initiate.modules.gen.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.gen.entity.GenScheme;

/**
 * 生成方案DAO接口


 */
@MyBatisDao
public interface GenSchemeDao extends CrudDao<GenScheme> {
	
}
