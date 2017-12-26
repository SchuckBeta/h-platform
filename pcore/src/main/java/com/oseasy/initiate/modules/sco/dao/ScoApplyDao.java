package com.oseasy.initiate.modules.sco.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sco.entity.ScoApply;

/**
 * 学分课程申请DAO接口.
 * @author zhangzheng
 * @version 2017-07-13
 */
@MyBatisDao
public interface ScoApplyDao extends CrudDao<ScoApply> {

}