package com.oseasy.initiate.modules.auditstandard.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.auditstandard.entity.AuditStandardDetailIns;

/**
 * 评审标准详情记录DAO接口.
 * @author 9527
 * @version 2017-07-28
 */
@MyBatisDao
public interface AuditStandardDetailInsDao extends CrudDao<AuditStandardDetailIns> {

}