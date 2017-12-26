package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwCosMaterialPrecords;

/**
 * 耗材购买记录DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwCosMaterialPrecordsDao extends CrudDao<PwCosMaterialPrecords> {

}