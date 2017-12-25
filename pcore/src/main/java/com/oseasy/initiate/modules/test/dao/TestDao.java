/**
 * 
 */
package com.oseasy.initiate.modules.test.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.test.entity.Test;

/**
 * 测试DAO接口


 */
@MyBatisDao
public interface TestDao extends CrudDao<Test> {
	
}
