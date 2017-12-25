package com.oseasy.initiate.modules.promodel.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.promodel.entity.ProModel;

/**
 * proModelDAO接口.
 * @author zy
 * @version 2017-07-13
 */
@MyBatisDao
public interface ProModelDao extends CrudDao<ProModel> {

}