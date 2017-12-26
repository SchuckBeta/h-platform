package com.oseasy.initiate.modules.promodel.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.promodel.entity.ActYwAuditInfo;

/**
 * 自定义审核信息DAO接口.
 * @author zy
 * @version 2017-11-01
 */
@MyBatisDao
public interface ActYwAuditInfoDao extends CrudDao<ActYwAuditInfo> {

}