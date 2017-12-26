package com.oseasy.initiate.modules.sys.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.SysCertificateRel;

/**
 * 系统证书资源关联DAO接口.
 * @author chenh
 * @version 2017-11-06
 */
@MyBatisDao
public interface SysCertificateRelDao extends CrudDao<SysCertificateRel> {

}