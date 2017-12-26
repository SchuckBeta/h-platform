/**
 * 
 */
package com.hch.platform.pcore.modules.gen.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.gen.entity.GenTemplate;

/**
 * 代码模板DAO接口


 */
@MyBatisDao
public interface GenTemplateDao extends CrudDao<GenTemplate> {
	
}
