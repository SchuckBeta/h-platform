package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRel;

/**
 * 入驻申报关联DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwEnterRelDao extends CrudDao<PwEnterRel> {

}