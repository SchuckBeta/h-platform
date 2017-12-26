package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.TreeDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwSpace;

/**
 * 设施DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwSpaceDao extends TreeDao<PwSpace> {

}