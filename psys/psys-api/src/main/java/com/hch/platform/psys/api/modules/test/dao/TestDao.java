/**
 * 
 */
package com.hch.platform.pcore.modules.test.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.test.entity.Test;

/**
 * 测试DAO接口


 */
@MyBatisDao
public interface TestDao extends CrudDao<Test> {
	
}
