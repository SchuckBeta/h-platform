package com.oseasy.initiate.modules.sco.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sco.entity.ScoAffirmCriterion;
import com.oseasy.initiate.modules.sco.vo.ScoAffrimCriterionVo;

/**
 * 学分认定标准DAO接口.
 * @author 9527
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAffirmCriterionDao extends CrudDao<ScoAffirmCriterion> {
    public ScoAffrimCriterionVo findCriter(ScoAffrimCriterionVo scoAffrimCriterionVo);
	public List<ScoAffirmCriterion> findListByConfid(String confid);
	public void delByConfid(String confid);
}