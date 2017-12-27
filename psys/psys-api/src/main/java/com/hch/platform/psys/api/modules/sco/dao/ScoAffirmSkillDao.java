package com.hch.platform.pcore.modules.sco.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmSkill;

/**
 * 技能学分认定DAO接口.
 * @author chenhao
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAffirmSkillDao extends CrudDao<ScoAffirmSkill> {

}