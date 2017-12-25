package com.oseasy.initiate.modules.actyw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;

/**
 * 项目流程表单DAO接口
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwFormDao extends CrudDao<ActYwForm> {
	
}