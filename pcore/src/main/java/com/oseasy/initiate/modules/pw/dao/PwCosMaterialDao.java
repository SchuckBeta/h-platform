package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwCosMaterial;

/**
 * 耗材DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwCosMaterialDao extends CrudDao<PwCosMaterial> {

}