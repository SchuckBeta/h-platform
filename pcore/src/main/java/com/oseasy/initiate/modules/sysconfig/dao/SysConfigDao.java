package com.hch.platform.pcore.modules.sysconfig.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sysconfig.entity.SysConfig;

/**
 * 系统配置DAO接口.
 * @author 9527
 * @version 2017-10-19
 */
@MyBatisDao
public interface SysConfigDao extends CrudDao<SysConfig> {
	public SysConfig getSysConfig();
}