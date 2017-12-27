package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.TreeDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwSpace;

/**
 * 设施DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwSpaceDao extends TreeDao<PwSpace> {

}