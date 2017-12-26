package com.oseasy.initiate.modules.sys.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.SysCertificateRes;

/**
 * 系统证书资源DAO接口.
 * @author chenh
 * @version 2017-11-06
 */
@MyBatisDao
public interface SysCertificateResDao extends CrudDao<SysCertificateRes> {

}