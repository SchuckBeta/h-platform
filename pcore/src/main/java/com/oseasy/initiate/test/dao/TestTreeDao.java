/**
 * 
 */
package com.oseasy.initiate.test.dao;

import com.oseasy.initiate.common.persistence.TreeDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.test.entity.TestTree;

/**
 * 树结构生成DAO接口

 * @version 2015-04-06
 */
@MyBatisDao
public interface TestTreeDao extends TreeDao<TestTree> {
	
}