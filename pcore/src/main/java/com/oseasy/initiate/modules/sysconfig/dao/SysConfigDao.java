package com.oseasy.initiate.modules.sysconfig.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sysconfig.entity.SysConfig;

/**
 * 系统配置DAO接口.
 * @author 9527
 * @version 2017-10-19
 */
@MyBatisDao
public interface SysConfigDao extends CrudDao<SysConfig> {
	public SysConfig getSysConfig();
}