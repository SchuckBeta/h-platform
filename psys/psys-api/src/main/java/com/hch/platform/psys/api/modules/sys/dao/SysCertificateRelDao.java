package com.hch.platform.pcore.modules.sys.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.SysCertificateRel;

/**
 * 系统证书资源关联DAO接口.
 * @author chenh
 * @version 2017-11-06
 */
@MyBatisDao
public interface SysCertificateRelDao extends CrudDao<SysCertificateRel> {

}