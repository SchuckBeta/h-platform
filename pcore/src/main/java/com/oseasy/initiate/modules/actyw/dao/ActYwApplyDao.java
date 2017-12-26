package com.oseasy.initiate.modules.actyw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwApply;
import com.oseasy.initiate.modules.actyw.vo.ActYwApplyVo;

/**
 * 流程申请DAO接口.
 * @author zy
 * @version 2017-12-05
 */
@MyBatisDao
public interface ActYwApplyDao extends CrudDao<ActYwApply> {

	void updateProcInsId(ActYwApplyVo actYwApply);
}