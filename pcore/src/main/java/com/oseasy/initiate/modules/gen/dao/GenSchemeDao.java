/**
 * 
 */
package com.hch.platform.pcore.modules.gen.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.gen.entity.GenScheme;

/**
 * 生成方案DAO接口


 */
@MyBatisDao
public interface GenSchemeDao extends CrudDao<GenScheme> {
	
}
