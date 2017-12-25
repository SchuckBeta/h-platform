package com.oseasy.initiate.modules.authorize.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.authorize.entity.SysLicense;

/**
 * 授权信息DAO接口
 * @author 9527
 * @version 2017-04-13
 */
@MyBatisDao
public interface SysLicenseDao extends CrudDao<SysLicense> {
	public void insertWithId(SysLicense s);
}