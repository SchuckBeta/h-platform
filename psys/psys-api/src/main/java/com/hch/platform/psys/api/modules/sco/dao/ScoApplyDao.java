package com.hch.platform.pcore.modules.sco.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoApply;

/**
 * 学分课程申请DAO接口.
 * @author zhangzheng
 * @version 2017-07-13
 */
@MyBatisDao
public interface ScoApplyDao extends CrudDao<ScoApply> {

}