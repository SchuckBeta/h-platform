package com.oseasy.initiate.modules.actyw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwConfig;

/**
 * 业务配置项DAO接口.
 * @author chenh
 * @version 2017-11-09
 */
@MyBatisDao
public interface ActYwConfigDao extends CrudDao<ActYwConfig> {

}