package com.oseasy.initiate.modules.actyw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;

/**
 * 时间和组件关联关系DAO接口.
 * @author zy
 * @version 2017-06-27
 */
@MyBatisDao
public interface ActYwGtimeDao extends CrudDao<ActYwGtime> {

	void deleteByGroupId(ActYwGtime actYwGtime);

	ActYwGtime getTimeByGnodeId(ActYwGtime actYwGtime);
}