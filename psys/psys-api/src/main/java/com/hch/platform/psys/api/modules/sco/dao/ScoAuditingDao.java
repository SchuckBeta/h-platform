package com.hch.platform.pcore.modules.sco.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoAuditing;

/**
 * 学分记录审核表DAO接口.
 * @author zhangzheng
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAuditingDao extends CrudDao<ScoAuditing> {

}