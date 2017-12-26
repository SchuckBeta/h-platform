package com.oseasy.initiate.modules.sco.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sco.entity.ScoAuditing;

/**
 * 学分记录审核表DAO接口.
 * @author zhangzheng
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAuditingDao extends CrudDao<ScoAuditing> {

}