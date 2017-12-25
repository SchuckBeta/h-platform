package com.oseasy.initiate.modules.interactive.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.interactive.entity.SysViews;

/**
 * 浏览表DAO接口.
 * @author 9527
 * @version 2017-06-30
 */
@MyBatisDao
public interface SysViewsDao extends CrudDao<SysViews> {

}