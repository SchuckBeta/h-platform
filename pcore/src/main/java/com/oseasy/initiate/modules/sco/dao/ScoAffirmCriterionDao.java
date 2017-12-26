package com.hch.platform.pcore.modules.sco.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmCriterion;
import com.hch.platform.pcore.modules.sco.vo.ScoAffrimCriterionVo;

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