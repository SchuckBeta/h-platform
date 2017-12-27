package com.hch.platform.pcore.modules.sco.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmCriterionCouse;

/**
 * 课程学分认定标准DAO接口.
 * @author 9527
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAffirmCriterionCouseDao extends CrudDao<ScoAffirmCriterionCouse> {
	public List<ScoAffirmCriterionCouse> findListByFid(String fid);
	public void delByFid(String fid);

	List<ScoAffirmCriterionCouse> findListByFidCouseNum(String fid);

	List<ScoAffirmCriterionCouse> findListByParentId(String parentId);
}