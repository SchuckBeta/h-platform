package com.hch.platform.pcore.modules.promodel.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.promodel.entity.ActYwAuditInfo;

/**
 * 自定义审核信息DAO接口.
 * @author zy
 * @version 2017-11-01
 */
@MyBatisDao
public interface ActYwAuditInfoDao extends CrudDao<ActYwAuditInfo> {

}