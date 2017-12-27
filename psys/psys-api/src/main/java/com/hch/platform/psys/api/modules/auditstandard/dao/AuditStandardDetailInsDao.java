package com.hch.platform.pcore.modules.auditstandard.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.auditstandard.entity.AuditStandardDetailIns;

/**
 * 评审标准详情记录DAO接口.
 * @author 9527
 * @version 2017-07-28
 */
@MyBatisDao
public interface AuditStandardDetailInsDao extends CrudDao<AuditStandardDetailIns> {

}