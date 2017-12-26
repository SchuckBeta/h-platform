package com.hch.platform.pcore.modules.authorize.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.authorize.entity.SysLicense;

/**
 * 授权信息DAO接口
 * @author 9527
 * @version 2017-04-13
 */
@MyBatisDao
public interface SysLicenseDao extends CrudDao<SysLicense> {
	public void insertWithId(SysLicense s);
}