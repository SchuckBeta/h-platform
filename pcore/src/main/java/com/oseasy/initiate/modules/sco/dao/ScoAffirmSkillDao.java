package com.oseasy.initiate.modules.sco.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sco.entity.ScoAffirmSkill;

/**
 * 技能学分认定DAO接口.
 * @author chenhao
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAffirmSkillDao extends CrudDao<ScoAffirmSkill> {

}