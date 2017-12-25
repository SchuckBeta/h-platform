package com.oseasy.initiate.modules.actyw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYw;

/**
 * 项目流程关联DAO接口
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwDao extends CrudDao<ActYw> {
	
}