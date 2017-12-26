package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwBillPrecords;

/**
 * 缴费记录DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwBillPrecordsDao extends CrudDao<PwBillPrecords> {

}