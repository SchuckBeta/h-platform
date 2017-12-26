package com.hch.platform.pcore.modules.sco.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmSkill;
import com.hch.platform.pcore.modules.sco.dao.ScoAffirmSkillDao;

/**
 * 技能学分认定Service.
 * @author chenhao
 * @version 2017-07-18
 */
@Service
@Transactional(readOnly = true)
public class ScoAffirmSkillService extends CrudService<ScoAffirmSkillDao, ScoAffirmSkill> {

	public ScoAffirmSkill get(String id) {
		return super.get(id);
	}

	public List<ScoAffirmSkill> findList(ScoAffirmSkill scoAffirmSkill) {
		return super.findList(scoAffirmSkill);
	}

	public Page<ScoAffirmSkill> findPage(Page<ScoAffirmSkill> page, ScoAffirmSkill scoAffirmSkill) {
		return super.findPage(page, scoAffirmSkill);
	}

	@Transactional(readOnly = false)
	public void save(ScoAffirmSkill scoAffirmSkill) {
		super.save(scoAffirmSkill);
	}

	@Transactional(readOnly = false)
	public void delete(ScoAffirmSkill scoAffirmSkill) {
		super.delete(scoAffirmSkill);
	}

}