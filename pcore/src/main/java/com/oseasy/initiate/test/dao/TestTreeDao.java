/**
 * 
 */
package com.hch.platform.pcore.test.dao;

import com.hch.platform.pcore.common.persistence.TreeDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.test.entity.TestTree;

/**
 * 树结构生成DAO接口

 * @version 2015-04-06
 */
@MyBatisDao
public interface TestTreeDao extends TreeDao<TestTree> {
	
}